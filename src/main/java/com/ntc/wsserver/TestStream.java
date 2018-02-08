/*
 * Copyright 2017 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntc.wsserver;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author nghiatc
 * @since Nov 22, 2017
 */
public class TestStream {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        Random r = new Random();
        for(int i = 0; i < 100; i++) {
            list.add(r.nextInt(1000000));
        }
        System.out.println("list: " + list);
        
    }

}
