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

import com.google.gson.Gson;
import java.io.ObjectStreamClass;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import org.apache.commons.math3.stat.inference.TTest;

/**
 *
 * @author Thibault Debatty
 */
public class CaseResult {

    private static final String CLASSPATH_KEY = "java.class.path";

    private String hostname;
    private String title = "";
    private final int processors;
    private final long memory;
    private final long time;
    private Case testcase;
    private final String[] classpath;

    private HashMap<FactoryAndValue, List<TestResult>> results;
    private final HashMap<TestFactory, String> sources;
    private long runtime;

    /**
     * Initialize the case result.
     */
    public CaseResult() {
        try {
            this.hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            this.hostname = "Unknown";
        }

        this.processors = Runtime.getRuntime().availableProcessors();
        this.memory = Runtime.getRuntime().maxMemory();
        this.time = System.currentTimeMillis();
        this.sources = new HashMap<TestFactory, String>();

        String classpath_string =
                ManagementFactory
                        .getRuntimeMXBean()
                        .getSystemProperties()
                        .get(CLASSPATH_KEY);
        this.classpath = classpath_string.split(":");
    }

    /**
     *
     * @return
     */
    public final String getHostname() {
        return hostname;
    }

    /**
     *
     * @param hostname
     */
    public final void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    /**
     *
     * @return
     */
    public final String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public final void setTitle(final String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public final Case getTestcase() {
        return testcase;
    }

    /**
     *
     * @param testcase
     */
    public final void setTestcase(final Case testcase) {
        this.testcase = testcase;
    }



    /**
     *
     * @return
     */
    public final double[] getParamValues() {
        return testcase.getParamValues();
    }

    /**
     *
     * @param results
     */
    public final void setResults(
            final HashMap<FactoryAndValue, List<TestResult>> results) {
        this.results = results;
    }


    /**
     *
     * @return
     */
    public final String time() {
        Date date = new Date(time);
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(date);
    }

    /**
     * Return the time id of this report.
     * @return
     */
    public final String getId() {
        Date date = new Date(time);
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss");
        return formater.format(date);
    }

    /**
     *
     * @return
     */
    public final String getCaseName() {
        return testcase.getClass().getName();
    }

    /**
     *
     * @return
     */
    public final String getCaseDescription() {
        return testcase.getDescription();
    }

    /**
     *
     * @return
     */
    public final long getCaseVersion() {
        return ObjectStreamClass
                .lookup(testcase.getClass())
                .getSerialVersionUID();
    }

    /**
     *
     * @return
     */
    public final List<TestFactory> getTests() {
        return testcase.getTests();
    }

    /**
     *
     * @return
     */
    public final List<TestResult> getResults() {
        LinkedList<TestResult> allresults = new LinkedList<TestResult>();
        for (List<TestResult> r : results.values()) {
            allresults.addAll(r);
        }

        return allresults;
    }

    /**
     *
     * @return
     */
    public final int getProcessors() {
        return processors;
    }

    /**
     *
     * @return
     */
    public final long getMemory() {
        return memory;
    }

    /**
     *
     * @return
     */
    public final String[] getClasspath() {
        return classpath;
    }

    /**
     *
     * @return
     */
    public final List<TestInterval> getIntervals() {
        LinkedList<TestInterval> intervals = new LinkedList<TestInterval>();
        for (FactoryAndValue test_and_value : results.keySet()) {
            intervals.add(
                    TestInterval.forResults(
                            test_and_value,
                            results.get(test_and_value)));
        }

        return intervals;
    }

    /**
     *
     * @return
     */
    public final int getValuesCount() {
        return results.entrySet().iterator().next()
                .getValue().iterator().next().getValues().length;
    }

    /**
     *
     * @param test
     * @param other_test
     * @param result
     * @param param_value
     * @return
     */
    public final double getSimilarity(
            final TestFactory test,
            final TestFactory other_test,
            final long result,
            final double param_value) {

        double[] test_results = new double[testcase.getIterations()];
        double[] other_results = new double[testcase.getIterations()];

        int i = 0;
        for (TestResult tr : results.get(new FactoryAndValue(test, param_value))) {
            test_results[i] = tr.getValue(result);
            i++;
        }

        i = 0;
        FactoryAndValue test_and_value = new FactoryAndValue(other_test, param_value);
        for (TestResult tr : results.get(test_and_value)) {
            other_results[i] = tr.getValue(result);
            i++;
        }

        TTest t_test = new TTest();
        return t_test.tTest(test_results, other_results);
    }

    /**
     *
     * @param test
     * @return
     */
    public final String getSource(final TestFactory test) {
        return sources.get(test);
    }

    /**
     * Add the source code of a test.
     * @param test
     * @param test_source
     */
    final void addSource(final TestFactory test, final String test_source) {
        sources.put(test, test_source);
    }

    /**
     * Get the JSON representation of the results, to use with GraphJS.
     * @param vid
     * @return
     */
    public final String getJsonDatasets(final long vid) {

        HashMap<TestFactory, Dataset> datasets =
                new HashMap<TestFactory, Dataset>();
        for (TestInterval interval : getIntervals()) {

            TestFactory test = interval.getTest();
            Dataset dataset = datasets.get(test);
            if (dataset == null) {
                dataset = new Dataset(
                        test.newInstance().getClass().getSimpleName());
                datasets.put(test, dataset);
            }
            dataset.add(new XY(
                    interval.getParamValue(),
                    interval.getValues()[(int) vid].getMean()));
        }

        Gson gson = new Gson();
        return gson.toJson(datasets.values());
    }

    /**
     * Get the JSON representation of time results.
     * @return
     */
    public final String getJsonTimeDataset() {

        HashMap<TestFactory, Dataset> datasets =
                new HashMap<TestFactory, Dataset>();
        for (TestInterval interval : getIntervals()) {

            TestFactory test = interval.getTest();
            Dataset dataset = datasets.get(test);
            if (dataset == null) {
                dataset = new Dataset(
                        test.newInstance().getClass().getSimpleName());
                datasets.put(test, dataset);
            }
            dataset.add(new XY(
                    interval.getParamValue(),
                    interval.getRuntime().getMean()));
        }

        Gson gson = new Gson();
        return gson.toJson(datasets.values());
    }

    /**
     *
     * @param runtime
     */
    final void setRuntime(final long runtime) {
        this.runtime = runtime;
    }

    /**
     * Get the (wall clock) total runtime of this case (in ms).
     * @return
     */
    public final long getRuntime() {
        return runtime;
    }
}

/**
 * Represents a single GraphJS dataset.
 * @author Thibault Debatty
 */
class Dataset {
    private final String label;
    private final TreeSet<XY> data = new TreeSet<XY>(new Comparator<XY>() {

        public int compare(final XY o1, final XY o2) {
            return o1.getX() >= o2.getX() ? 1 : -1;
        }
    });

    Dataset(final String label) {
        this.label = label;
    }

    void add(final XY xy) {
        data.add(xy);

    }
}

/**
 * Represents a single GraphJS point.
 * @author Thibault Debatty
 */
class XY {
    private final double x;
    private final double y;

    XY(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Return x value.
     * @return
     */
    public double getX() {
        return x;
    }
}
