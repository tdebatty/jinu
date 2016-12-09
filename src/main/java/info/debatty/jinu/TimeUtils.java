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
public final class TimeUtils {

    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUTE = ONE_SECOND * 60;
    private static final long ONE_HOUR = ONE_MINUTE * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;

    private TimeUtils() {
    }

    /**
     * converts time (in milliseconds) to human-readable format "<w> days, <x>
     * hours, <y> minutes and <z> seconds".
     *
     * @param duration
     * @return
     */
    public static String millisToDHMS(final long duration) {
        StringBuilder res = new StringBuilder();
        long temp = 0;
        long remaining_duration = duration;
        if (remaining_duration >= ONE_SECOND) {
            temp = remaining_duration / ONE_DAY;
            if (temp > 0) {
                remaining_duration -= temp * ONE_DAY;
                res.append(temp).append(" day").append(temp > 1 ? "s" : "")
                        .append(remaining_duration >= ONE_MINUTE ? ", " : "");
            }

            temp = remaining_duration / ONE_HOUR;
            if (temp > 0) {
                remaining_duration -= temp * ONE_HOUR;
                res.append(temp).append(" hour").append(temp > 1 ? "s" : "")
                        .append(remaining_duration >= ONE_MINUTE ? ", " : "");
            }

            temp = remaining_duration / ONE_MINUTE;
            if (temp > 0) {
                remaining_duration -= temp * ONE_MINUTE;
                res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
            }

            if (!res.toString().equals("") && remaining_duration >= ONE_SECOND) {
                res.append(" and ");
            }

            temp = remaining_duration / ONE_SECOND;
            if (temp > 0) {
                res.append(temp).append(" second").append(temp > 1 ? "s" : "");
            }
            return res.toString();

        } else {
            return "0 second";
        }
    }
}
