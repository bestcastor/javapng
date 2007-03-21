/*
com.sixlegs.png - Java package to read and display PNG images
Copyright (C) 1998-2006 Chris Nokleberg

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version.
*/

package com.sixlegs.png.examples;

import java.util.*;

class ArgumentProcessor
{
    private static final Map<String,Parser> PARSERS = new HashMap<String,Parser>();

    static
    {
        PARSERS.put(Boolean.class.getName(), new Parser() {
            public Object parse(String arg) { return Boolean.valueOf(arg); }
        });
        PARSERS.put(String.class.getName(), new Parser() {
            public Object parse(String arg) { return arg; }
        });
        PARSERS.put(Integer.class.getName(), new Parser() {
            public Object parse(String arg) { return Integer.valueOf(arg, 10); }
        });
        PARSERS.put(Short.class.getName(), new Parser() {
            public Object parse(String arg) { return Short.valueOf(arg, 10); }
        });
        PARSERS.put(Long.class.getName(), new Parser() {
            public Object parse(String arg) { return Long.valueOf(arg, 10); }
        });
        PARSERS.put(Character.class.getName(), new Parser() {
            public Object parse(String arg) {
                if (arg.length() != 1)
                    throw new IllegalArgumentException("requires single character");
                return arg.charAt(0);
            }
        });
    }

    abstract private static class Parser
    {
        abstract public Object parse(String arg);
    }
    
    private final Map<String,Option> options = new HashMap<String,Option>();
    
    public ArgumentProcessor(List<Option> options)
    {
        for (Option opt : options)
            this.options.put(opt.name, new Option(opt));
    }

    // mutates args
    // throws exception if arguments are invalid
    public Map<String,Object> parse(List<String> src, List<String> dst)
    {
        Map<String,Object> result = new HashMap<String,Object>();
        int index = 0;
        while (index < src.size()) {
            String arg = src.get(index);
            if (arg.startsWith("--")) {
                index++;
                if (arg.equals("--"))
                    break;
                String name = arg.substring(2);
                Option opt = options.get(name);
                if (opt == null)
                    throw new IllegalArgumentException("Unknown argument " + arg);

                String peek = (index < src.size()) ? src.get(index) : null;
                boolean hasArg = peek != null && !peek.startsWith("--") && !peek.equals("--");
                if (hasArg) {
                    if (opt.type == null)
                        throw new IllegalArgumentException("Option " + name + " does not take an argument");
                    index++;
                    result.put(name, opt.parse(peek));
                } else {
                    if (opt.type != null)
                        throw new IllegalArgumentException("Expecting argument for option " + name);
                }
            } else {
                break;
            }
        }
        for (Option opt : options.values()) {
            if (opt.required) {
                if (!result.containsKey(opt.name))
                    throw new IllegalArgumentException("--" + opt.name + " is required");
            } else if (opt.type != null) {
                result.put(opt.name, opt.defaultValue);
            }
        }
        List<String> tmp = new ArrayList<String>();
        tmp.addAll(src.subList(index, src.size()));
        dst.clear();
        dst.addAll(tmp);
        return result;
    }

    public static class Option
    {
        final String name;
        final Class type;
        boolean required;
        Object defaultValue;
        Comparable start;
        Comparable end;

        Option(Option option)
        {
            name = option.name;
            type = option.type;
            required = option.required;
            defaultValue = option.defaultValue;
            start = option.start;
            end = option.end;
        }

        public Option(String name)
        {
            this(name, null, false);
        }

        public Option(String name, Class type)
        {
            this(name, type, true);
            if (type == null)
                throw new IllegalArgumentException("Type cannot be null");
        }

        private Option(String name, Class type, boolean required)
        {
            if (name.startsWith("-"))
                throw new IllegalArgumentException("Option names cannot start with a hyphen");
            if (!PARSERS.containsKey(type.getName()))
                throw new IllegalArgumentException("Unsupported type " + type);
            this.name = name;
            this.type = type;
            this.required = required;
        }

        public Option defaultValue(Object value)
        {
            checkType(value);
            required = false;
            defaultValue = value;
            return this;
        }

        public Option range(Comparable start, Comparable end)
        {
            checkType(start);
            checkType(end);
            if (!Comparable.class.isAssignableFrom(type))
                throw new IllegalArgumentException(type + " is not Comparable");
            if (start.compareTo(end) >= 0)
                throw new IllegalArgumentException(start + " is not less than " + end);
            this.start = start;
            this.end = end;
            return this;
        }

        private Object parse(String arg)
        {
            try {
                Object value = PARSERS.get(type.getName()).parse(arg);
                if (start != null) {
                    Comparable comp = (Comparable)value;
                    if (comp.compareTo(start) < 0 || comp.compareTo(end) > 0)
                        throw new IllegalArgumentException(value + " is not between " + start + " and " + end);
                }
                return value;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(name + " " + e.getMessage());
            }
        }

        private void checkType(Object value)
        {
            if (type == null)
                throw new IllegalArgumentException(name + " does not take an argument");
            if (!type.isAssignableFrom(value.getClass()))
                throw new IllegalArgumentException(name + " value " + value + " is not assignable to " + type.getName());
        }
    }
}
