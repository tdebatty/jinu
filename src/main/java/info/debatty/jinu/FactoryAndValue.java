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
public class FactoryAndValue {

    private final TestFactory factory;
    private final double value;

    FactoryAndValue(final TestFactory factory, final double param_value) {
        this.factory = factory;
        this.value = param_value;
    }

    /**
     *
     * @return
     */
    public final TestFactory getTest() {
        return factory;
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
        hash = 19 * hash + this.factory.hashCode();
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
        final FactoryAndValue other = (FactoryAndValue) obj;

        if (
                Double.doubleToLongBits(this.value)
                != Double.doubleToLongBits(other.value)) {
            return false;
        }

        return !(this.factory != other.factory
                && (this.factory == null
                || !this.factory.equals(other.factory)));
    }



}
