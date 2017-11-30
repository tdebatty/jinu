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

package info.debatty.jinu.examples;

import info.debatty.jinu.Case;

/**
 *
 * @author Thibault Debatty
 */
public class TestCase1Iteration extends Case {

    /**
     * Run the tests.
     * @param args
     * @throws Exception if one of the tests fails.
     */
    public static void main(final String[] args) throws Exception {

        Case test = new TestCase1Iteration();
        test.setDescription("TestCase1Iteration, with args: " + args.length);
        test.run();
    }

    /**
     * Default case.
     */
    public TestCase1Iteration() {
        super();
        super.commitToGit(false);
        setIterations(1);
        setParamValues(new double[]{2.0, 3.0, 4.0});
        addTest(DummyTest.class);
        addTest(DummyTest2.class);
    }
}
