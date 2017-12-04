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

import java.util.List;

/**
 * All the intervals for a test.
 * @author Thibault Debatty
 */
public final class TestInterval {

    private double param_value;
    private final SummaryStatistics[] values;
    private final SummaryStatistics runtime;
    private TestFactory test;

    /**
     *
     * @param length
     */
    TestInterval(final int length) {
        runtime = new SummaryStatistics();
        values = new SummaryStatistics[length];
        for (int i = 0; i < length; i++) {
            values[i] = new SummaryStatistics();
        }
    }

    /**
     *
     */
    private TestInterval() {
        this(0);
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
    public SummaryStatistics[] getValues() {
        return values;
    }

    /**
     *
     * @return
     */
    public SummaryStatistics getRuntime() {
        return runtime;
    }

    /**
     *
     * @return
     */
    public TestFactory getTest() {
        return test;
    }

    /**
     *
     * @param test_and_value
     * @param results
     * @return
     */
    public static TestInterval forResults(
            final FactoryAndValue test_and_value,
            final List<TestResult> results) {

        int values_count = results.iterator().next().getValues().length;

        TestInterval test_interval = new TestInterval(values_count);
        test_interval.test = test_and_value.getTest();
        test_interval.param_value = test_and_value.getValue();

        // Add all values
        for (TestResult result : results) {
            test_interval.runtime.addValue(result.getRuntime());

            for (int i = 0; i < values_count; i++) {
                test_interval.values[i].addValue(result.getValue(i));
            }
        }

        return test_interval;
    }
}
