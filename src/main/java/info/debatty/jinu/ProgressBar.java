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
public class ProgressBar {

    private static final double HUNDRED = 100.0;

    private final int max;
    private long start_time = 0;


    /**
     *
     * @param max
     */
    public ProgressBar(final int max) {
        this.max = max;
    }

    /**
     * Start the timer...
     */
    public final void start() {
        this.start_time = System.currentTimeMillis();
    }

    /**
     * Update the internal counter, and display ETR.
     * @param value
     */
    public final void update(final int value) {

        long time = System.currentTimeMillis();
        long etr = (max - value) * (time - start_time) / value;

        int ratio = (int) HUNDRED * value / max;
        System.out.printf(
                "%3d%% %s remaining\r", ratio, TimeUtils.millisToDHMS(etr));
    }

}
