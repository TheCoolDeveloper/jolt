/*
 * Copyright 2013 Bazaarvoice, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bazaarvoice.jolt.modifier.function;

import com.bazaarvoice.jolt.common.Optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings( "deprecated" )
public class Math {

    /**
     * Given a list of objects, returns the max value in its appropriate type
     * also, interprets String as Number and returns appropriately
     *
     * max(1,2l,3d) == Optional.of(3d)
     * max(1,2l,"3.0") == Optional.of(3.0)
     * max("a", "b", "c") == Optional.empty()
     * max([]) == Optional.empty()
     */
    public static Optional<Number> max( List<Object> args ) {
        if(args.size() == 0) {
            return Optional.empty();
        }

        Integer maxInt = Integer.MIN_VALUE;
        Double maxDouble = -(Double.MAX_VALUE);
        Long maxLong = Long.MIN_VALUE;
        boolean found = false;

        for(Object arg: args) {
            if(arg instanceof Integer) {
                maxInt = java.lang.Math.max( maxInt, (Integer) arg );
                found = true;
            }
            else if(arg instanceof Double) {
                maxDouble = java.lang.Math.max( maxDouble, (Double) arg );
                found = true;
            }
            else if(arg instanceof Long) {
                maxLong = java.lang.Math.max(maxLong, (Long) arg);
                found = true;
            }
            else if(arg instanceof String) {
                Optional<?> optional = toNumber( arg );
                if(optional.isPresent()) {
                    arg = optional.get();
                    if(arg instanceof Integer) {
                        maxInt = java.lang.Math.max( maxInt, (Integer) arg );
                        found = true;
                    }
                    else if(arg instanceof Double) {
                        maxDouble = java.lang.Math.max( maxDouble, (Double) arg );
                        found = true;
                    }
                    else if(arg instanceof Long) {
                        maxLong = java.lang.Math.max(maxLong, (Long) arg);
                        found = true;
                    }
                }
            }
        }
        if(!found) {
            return Optional.empty();
        }

        // explicit getter method calls to avoid runtime autoboxing
        // autoBoxing will cause it to return the different type
        // check MathTest#testAutoBoxingIssue for example
        if(maxInt.longValue() >= maxDouble.longValue() && maxInt.longValue() >= maxLong) {
            return Optional.<Number>of(maxInt);
        }
        else if(maxLong >= maxDouble.longValue()) {
            return Optional.<Number>of(maxLong);
        }
        else {
            return Optional.<Number>of(maxDouble);
        }
    }

    /**
     * Given a list of objects, returns the min value in its appropriate type
     * also, interprets String as Number and returns appropriately
     *
     * min(1d,2l,3) == Optional.of(1d)
     * min("1.0",2l,d) == Optional.of(1.0)
     * min("a", "b", "c") == Optional.empty()
     * min([]) == Optional.empty()
     */
    public static Optional<Number> min( List<Object> args ) {
        if(args.size() == 0) {
            return Optional.empty();
        }
        Integer minInt = Integer.MAX_VALUE;
        Double minDouble = Double.MAX_VALUE;
        Long minLong = Long.MAX_VALUE;
        boolean found = false;

        for(Object arg: args) {
            if(arg instanceof Integer) {
                minInt = java.lang.Math.min( minInt, (Integer) arg );
                found = true;
            }
            else if(arg instanceof Double) {
                minDouble = java.lang.Math.min( minDouble, (Double) arg );
                found = true;
            }
            else if(arg instanceof Long) {
                minLong = java.lang.Math.min( minLong, (Long) arg );
                found = true;
            }
            else if(arg instanceof String) {
                Optional<?> optional = toNumber( arg );
                if(optional.isPresent()) {
                    arg = optional.get();
                    if(arg instanceof Integer) {
                        minInt = java.lang.Math.min( minInt, (Integer) arg );
                        found = true;
                    }
                    else if(arg instanceof Double) {
                        minDouble = java.lang.Math.min( minDouble, (Double) arg );
                        found = true;
                    }
                    else if(arg instanceof Long) {
                        minLong = java.lang.Math.min(minLong, (Long) arg);
                        found = true;
                    }
                }
            }
        }
        if(!found) {
            return Optional.empty();
        }
        // explicit getter method calls to avoid runtime autoboxing
        if(minInt.longValue() <= minDouble.longValue() && minInt.longValue() <= minLong) {
            return Optional.<Number>of(minInt);
        }
        else if(minLong <= minDouble.longValue()) {
            return Optional.<Number>of(minLong);
        }
        else {
            return Optional.<Number>of(minDouble);
        }
    }

    /**
     * Given any object, returns, if possible. its absolute value wrapped in Optional
     * Interprets String as Number
     *
     * abs("-123") == Optional.of(123)
     * abs("123") == Optional.of(123)
     * abs("12.3") == Optional.of(12.3)
     *
     * abs("abc") == Optional.empty()
     * abs(null) == Optional.empty()
     *
     */
    public static Optional<Number> abs( Object arg ) {
        if(arg instanceof Integer) {
            return Optional.<Number>of( java.lang.Math.abs( (Integer) arg ));
        }
        else if(arg instanceof Double) {
            return Optional.<Number>of( java.lang.Math.abs( (Double) arg ));
        }
        else if(arg instanceof Long) {
            return Optional.<Number>of( java.lang.Math.abs( (Long) arg ));
        }
        else if(arg instanceof String) {
            return abs( toNumber( arg ).get() );
        }
        return Optional.empty();
    }

    /**
     * Given any object, returns, if possible. its Java number equivalent wrapped in Optional
     * Interprets String as Number
     *
     * toNumber("123") == Optional.of(123)
     * toNumber("-123") == Optional.of(-123)
     * toNumber("12.3") == Optional.of(12.3)
     *
     * toNumber("abc") == Optional.empty()
     * toNumber(null) == Optional.empty()
     *
     * also, see: MathTest#testNitPicks
     *
     */
    public static Optional<? extends Number> toNumber(Object arg) {
        if ( arg instanceof Number ) {
            return Optional.of( ( (Number) arg ));
        }
        else if(arg instanceof String) {
            try {
                return Optional.of( (Number) Integer.parseInt( (String) arg ) );
            }
            catch(Exception ignored) {}
            try {
                return Optional.of( (Number) Long.parseLong( (String) arg ) );
            }
            catch(Exception ignored) {}
            try {
                return Optional.of( (Number) Double.parseDouble( (String) arg ) );
            }
            catch(Exception ignored) {}
            return Optional.empty();
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Returns int value of argument, if possible, wrapped in Optional
     * Interprets String as Number
     */
    public static Optional<Integer> toInteger(Object arg) {
        if ( arg instanceof Number ) {
            return Optional.of( ( (Number) arg ).intValue() );
        }
        else if(arg instanceof String) {
            Optional<? extends Number> optional = toNumber( arg );
            if ( optional.isPresent() ) {
                return Optional.of( optional.get().intValue() );
            }
            else {
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Returns long value of argument, if possible, wrapped in Optional
     * Interprets String as Number
     */
    public static Optional<Long> toLong(Object arg) {
        if ( arg instanceof Number ) {
            return Optional.of( ( (Number) arg ).longValue() );
        }
        else if(arg instanceof String) {
            Optional<? extends Number> optional = toNumber( arg );
            if ( optional.isPresent() ) {
                return Optional.of( optional.get().longValue() );
            }
            else {
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Returns double value of argument, if possible, wrapped in Optional
     * Interprets String as Number
     */
    public static Optional<Double> toDouble(Object arg) {
        if ( arg instanceof Number ) {
            return Optional.of( ( (Number) arg ).doubleValue() );
        }
        else if(arg instanceof String) {
            Optional<? extends Number> optional = toNumber( arg );
            if ( optional.isPresent() ) {
                return Optional.of( optional.get().doubleValue() );
            }
            else {
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }

    @SuppressWarnings( "unchecked" )
    public static final class max implements Function {

        public Optional<Object> apply( final List<Object> args ) {
            return (Optional) max( args );
        }

        @Override
        public Optional<Object> apply( final Object... args ) {
            return (Optional) max( Arrays.asList( args ));
        }

    }

    @SuppressWarnings( "unchecked" )
    public static final class min implements Function {

        public Optional<Object> apply( final List<Object> args ) {
            return (Optional) min( args );
        }

        @Override
        public Optional<Object> apply( final Object... args ) {
            return (Optional) min( Arrays.asList( args ));
        }
    }

    @SuppressWarnings( "unchecked" )
    public static final class abs extends genericConverter {
        @Override
        protected <T extends Number> Optional<T> convert( final Object o ) {
            return (Optional) abs( o );
        }
    }

    @SuppressWarnings( "unchecked" )
    public static final class toInteger extends genericConverter {
        @Override
        protected <T extends Number> Optional<T> convert( final Object o ) {
            return (Optional<T>) toInteger( o );
        }

    }

    @SuppressWarnings( "unchecked" )
    public static final class toLong extends genericConverter {
        @Override
        protected <T extends Number> Optional<T> convert( final Object o ) {
            return (Optional<T>) toLong( o );
        }
    }

    @SuppressWarnings( "unchecked" )
    public static final class toDouble extends genericConverter {
        @Override
        protected <T extends Number> Optional<T> convert( final Object o ) {
            return (Optional<T>) toDouble( o );
        }
    }

    @SuppressWarnings( "unchecked" )
    private static abstract class genericConverter implements Function {

        public Optional<Object> apply( final Object... args ) {
            if(args.length == 0) {
                return Optional.empty();
            }
            else if(args.length == 1) {
                return (Optional) convert( args[0] );
            }
            return apply(Arrays.asList( args ));
        }

        public Optional<Object> apply( final List<Object> input ) {
            List<Object> ret = new ArrayList<>( input.size() );
            for(Object o: input) {
                Optional<? extends Number> optional = convert( o );
                ret.add(optional.isPresent()?optional.get():o);
            }
            return Optional.<Object>of( ret );
        }

        protected abstract <T extends Number> Optional<T> convert( final Object o );
    }

}
