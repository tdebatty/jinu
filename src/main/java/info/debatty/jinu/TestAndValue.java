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
 * Captures a test instance, and the value that was fed to the test.
 * @author Thibault Debatty
 */
public class TestAndValue {

    private final TestInterface test;
    private final double value;

    TestAndValue(final TestInterface test, final double param_value) {
        this.test = test;
        this.value = param_value;
    }

    /**
     *
     * @return
     */
    public final TestInterface getTest() {
        return test;
    }

    /**
     *
     * @return
     */
    public final double getValue() {
        return value;
    }

    @Override
    public final int hashCode() {
        int hash = 5;
        hash = 19 * hash + this.test.hashCode();
        hash = 19 * hash
                + (int) (Double.doubleToLongBits(this.value)
                ^ (Double.doubleToLongBits(this.value) >>> 32));
        return hash;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestAndValue other = (TestAndValue) obj;

        if (
                Double.doubleToLongBits(this.value)
                != Double.doubleToLongBits(other.value)) {
            return false;
        }

        if (
                this.test != other.test
                && (this.test == null || !this.test.equals(other.test))) {
            return false;
        }
        return true;
    }



}
