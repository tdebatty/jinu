/*
 * The MIT License
 *
 * Copyright 2016 Thibault Debatty.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package info.debatty.jinu;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 *
 * @author Thibault Debatty
 */
public class Case implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(Case.class.getName());

    private int iterations;
    private final LinkedList<TestInterface> tests =
            new LinkedList<TestInterface>();
    private double[] param_values = null;

    /**
     * Set the values that will be passed to tests.
     * @param param_values
     */
    public final void setParamValues(final double[] param_values) {
        this.param_values = param_values;
    }

    /**
     * Set the number of iterations for running tests.
     * @param iterations
     */
    public final void setIterations(final int iterations) {
        this.iterations = iterations;
    }

    /**
     * Add a test to the case.
     * @param testclass
     */
    public final void addTest(final  Class<? extends TestInterface> testclass) {
        try {
            tests.add(testclass.newInstance());
        } catch (InstantiationException ex) {
            Logger.getLogger(Case.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Case.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Run the tests.
     * @throws java.io.FileNotFoundException if reports cannot be written.
     * @throws Exception if one of the tests throws an Exception
     */
    public final void run() throws FileNotFoundException, Exception {

        Date date = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat day_formater = new SimpleDateFormat("yyyyMMdd");
        String time_tag = formater.format(date);
        String day_tag = day_formater.format(date);
        String filename = day_tag + File.separator + time_tag + ".html";

        File directory = new File(day_tag);
        if (!directory.exists()) {
            directory.mkdir();
        }

        CaseResult report = createReport();
        HashMap<TestAndValue, List<TestResult>> results =
                new HashMap<TestAndValue, List<TestResult>>();

        ExecutorService threadpool = Executors.newFixedThreadPool(4);

        ProgressBar progress = new ProgressBar(iterations);
        progress.start();

        for (int i = 0; i < iterations; i++) {
            if (param_values == null) {
                // No param to give to the tests...
                param_values = new double[]{0};
            }

            ArrayList<Future> tasks = new ArrayList<Future>();

            for (TestInterface test : tests) {

                for (double param_value : param_values) {
                    TestAndValue key = new TestAndValue(test, param_value);
                    List<TestResult> resultset = results.get(key);
                    if (resultset == null) {
                        resultset = Collections.synchronizedList(
                                new LinkedList<TestResult>());
                        results.put(key, resultset);
                    }

                    tasks.add(threadpool.submit(
                            new RunnableTest(test, param_value, resultset)));
                }
            }

            for (Future task : tasks) {
                task.get();
            }
            progress.update(i + 1);
        }


        report.setResults(results);

        PebbleEngine engine = new PebbleEngine.Builder().build();
        Writer writer = new StringWriter();
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("report", report);

        try {
            PebbleTemplate template =
                    engine.getTemplate("templates/report.twig");
            template.evaluate(writer, context);

        } catch (PebbleException ex) {
            LOGGER.log(Level.WARNING, "Cannot produce html report!", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Cannot produce html report!", ex);
        }

        PrintWriter out = new PrintWriter(filename);
        out.println(writer.toString());
        out.close();
        launchBrowser(filename);

        // write data
        String data_filename = formater.format(date) + ".dat";
        PrintWriter data_writer = new PrintWriter(data_filename);
        for (List<TestResult> resultlist : results.values()) {
            for (TestResult result : resultlist) {
                data_writer.write(result.toCsv() + "\n");
            }
        }
        data_writer.close();

        // Find the git root
        Repository repo = new FileRepositoryBuilder()
                .findGitDir()
                .build();

        Git git = new Git(repo);
        git.add().addFilepattern(".").call();
        git.commit().setAll(true).setMessage("Test case " + time_tag).call();
        git.tag().setName("T" + time_tag).call();

    }

    private CaseResult createReport() {

        CaseResult report = new CaseResult();
        report.setTitle(this.getClass().getName());
        report.setTestcase(this);

        // Try to read the source code of the tests
        for (TestInterface test : tests) {

            Class clazz = test.getClass();
            String path = "src/main/java/"
                    + clazz.getPackage().getName().replaceAll("\\.", "/");
            File source_file = new File(path, clazz.getSimpleName() + ".java");
            try {
                List<String> alllines =
                        Files.readAllLines(
                                source_file.toPath(),
                                Charset.defaultCharset());
                StringBuilder builder = new StringBuilder();
                for (String line : alllines) {
                    builder.append(line);
                    builder.append("\n");
                }

                report.addSource(test, builder.toString());
            } catch (IOException ex) {
                LOGGER.log(
                        Level.INFO,
                        "Cannot read source of " + clazz.getName(),
                        ex);
            }
        }

        return report;

    }

    /**
     * Write the environemnt variables to a PrintWriter.
     * @param writer
     */
    public final void writeEnvironment(final PrintWriter writer) {

        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        Map<String, String> properties = runtime.getSystemProperties();
        ArrayList<String> keys = new ArrayList<String>(properties.keySet());
        Collections.sort(keys);

        writer.write("<h2>Environment</h2>");
        writer.write("<pre>");
        for (String key : keys) {
            String value = properties.get(key);
            writer.printf("%s : %s.\n", key, value);
        }
        writer.write("</pre>");
    }

    /**
     *
     * @return
     */
    public final double[] getParamValues() {
        return param_values;
    }

    /**
     *
     * @return
     */
    public final LinkedList<TestInterface> getTests() {
        return tests;
    }

    /**
     *
     * @return
     */
    public final int getIterations() {
        return iterations;
    }



    private void launchBrowser(final String filename) {
        if (Desktop.isDesktopSupported()) {
            File file = new File(filename);
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(file.toURI());

            } catch (IOException e) {
                LOGGER.log(Level.INFO, "Cannot launch brower", e);
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + filename);

            } catch (IOException e) {
                LOGGER.log(Level.INFO, "Cannot launch brower", e);
            }
        }
    }
}

/**
 * A wrapper around the task, to be submitted to the executor.
 * @author tibo
 */
class RunnableTest implements Runnable {

    private final TestInterface test;
    private final double value;
    private final List<TestResult> resultset;

    RunnableTest(
            final TestInterface test,
            final double value,
            final List<TestResult> resultset) {
        this.test = test;
        this.value = value;
        this.resultset = resultset;
    }

    public void run() {
        try {
            long start_time = System.currentTimeMillis();
            double[] values = test.run(value);
            long runtime = System.currentTimeMillis() - start_time;
            resultset.add(
                    new TestResult(values, runtime, test, value));

            System.gc();
            System.runFinalization();
        } catch (Exception ex) {
            Logger.getLogger(
                    RunnableTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
