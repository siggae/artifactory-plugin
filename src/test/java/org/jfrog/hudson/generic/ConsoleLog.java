/*
 * Copyright (C) 2011 JFrog Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jfrog.hudson.generic;

import org.jfrog.build.api.util.Log;

/**
 * A log implementation that doesn't do anything
 *
 * @author Noam Y. Tenne
 */
public class ConsoleLog implements Log {

    public void debug(String message) {
        System.out.println(message);
    }

    public void info(String message) {
    	System.out.println(message);
    }

    public void warn(String message) {
    	System.out.println(message);
    }

    public void error(String message) {
    	System.err.println(message);
    }

    public void error(String message, Throwable e) {
    	System.err.println(message + " " + e.getMessage());
    }
}
