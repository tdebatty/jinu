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

/**
 *
 * @author Thibault Debatty
 */
public final class TestResult {

    private static final String SEPARATOR = ";\t";

    private final double[] values;
    private final long runtime;
    private final TestInterface test;
    private final double param_value;

    /**
     *
     * @param values
     * @param runtime
     * @param test
     * @param param_value
     */
    public TestResult(
            final double[] values,
            final long runtime,
            final TestInterface test,
            final double param_value) {

        this.values = values;
        this.runtime = runtime;
        this.test = test;
        this.param_value = param_value;
    }

    /**
     *
     * @return
     */
    public TestInterface getTest() {
        return test;
    }

    /**
     *
     * @return
     */
    public double getParamValue() {
        return param_value;
    }



    /**
     *
     * @return
     */
    public double[] getValues() {
        return values;
    }

    /**
     *
     * @param position
     * @return
     */
    public double getValue(final long position) {
        return values[(int) position];
    }

    /**
     *
     * @return
     */
    public long getRuntime() {
        return runtime;
    }

    /**
     *
     * @return
     */
    public String toCsv() {
        return test.getClass().getName() + SEPARATOR
                + param_value + SEPARATOR
                + runtime + SEPARATOR
                + arrToCsv(values);
    }

    /**
     *
     * @return
     */
    public String getHeader() {
        String r = "## test" + SEPARATOR
                + "input value" + SEPARATOR
                + "runtime" + SEPARATOR;

        for (int i = 0; i < values.length; i++) {
            r += "value" + i + SEPARATOR;
        }

        r += "\n";

        return r;
    }

    private static String arrToCsv(final double[] arr) {
        String r = "";
        for (int i = 0; i < arr.length; i++) {
            r += arr[i] + SEPARATOR;
        }
        return r;
    }
}
