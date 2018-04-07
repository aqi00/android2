/*
   2    * Copyright (c) 1995, 1997, Oracle and/or its affiliates. All rights reserved.
   3    * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
   4    *
   5    * This code is free software; you can redistribute it and/or modify it
   6    * under the terms of the GNU General Public License version 2 only, as
   7    * published by the Free Software Foundation.  Oracle designates this
   8    * particular file as subject to the "Classpath" exception as provided
   9    * by Oracle in the LICENSE file that accompanied this code.
  10    *
  11    * This code is distributed in the hope that it will be useful, but WITHOUT
  12    * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  13    * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
  14    * version 2 for more details (a copy is included in the LICENSE file that
  15    * accompanied this code).
  16    *
  17    * You should have received a copy of the GNU General Public License version
  18    * 2 along with this work; if not, write to the Free Software Foundation,
  19    * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
  20    *
  21    * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
  22    * or visit www.oracle.com if you need additional information or have any
  23    * questions.
  24    */
package com.example.test.encrypt.base64;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;

/**
 * This class implements a BASE64 Character encoder as specified in RFC1521.
 * This RFC is part of the MIME specification as published by the Internet
 * Engineering Task Force (IETF). Unlike some other encoding schemes there is
 * nothing in this encoding that indicates where a buffer starts or ends.
 * <p>
 * This means that the encoded text will simply start with the first line of
 * encoded text and end with the last line of encoded text.
 *
 * @author Chuck McManis
 * @see CharacterEncoder
 * @see BASE64Decoder
 */
public class BASE64Encoder extends CharacterEncoder {

    /**
     * this class encodes three bytes per atom.
     */
    protected int bytesPerAtom() {
        return (3);
    }

    /**
     * this class encodes 57 bytes per line. This results in a maximum of 57/3 *
     * 4 or 76 characters per output line. Not counting the line termination.
     */
    protected int bytesPerLine() {
        return (57);
    }

    /**
     * This array maps the characters to their 6 bit values
     */
    private final static char pem_array[] =
            {
                    // 0 1 2 3 4 5 6 7
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', // 0
                    'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', // 1
                    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', // 2
                    'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', // 3
                    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', // 4
                    'o', 'p', 'q', 'r', 's', 't', 'u', 'v', // 5
                    'w', 'x', 'y', 'z', '0', '1', '2', '3', // 6
                    '4', '5', '6', '7', '8', '9', '+', '/' // 7
            };

    /**
     * encodeAtom - Take three bytes of input and encode it as 4 printable
     * characters. Note that if the length in len is less than three is encodes
     * either one or two '=' signs to indicate padding characters.
     */
    protected void encodeAtom(OutputStream outStream, byte data[], int offset,
                              int len) throws IOException {
        byte a, b, c;
        if (len == 1) {
            a = data[offset];
            b = 0;
            c = 0;
            outStream.write(pem_array[(a >>> 2) & 0x3F]);
            outStream.write(pem_array[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
            outStream.write('=');
            outStream.write('=');
        } else if (len == 2) {
            a = data[offset];
            b = data[offset + 1];
            c = 0;
            outStream.write(pem_array[(a >>> 2) & 0x3F]);
            outStream.write(pem_array[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
            outStream.write(pem_array[((b << 2) & 0x3c) + ((c >>> 6) & 0x3)]);
            outStream.write('=');
        } else {
            a = data[offset];
            b = data[offset + 1];
            c = data[offset + 2];
            outStream.write(pem_array[(a >>> 2) & 0x3F]);
            outStream.write(pem_array[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
            outStream.write(pem_array[((b << 2) & 0x3c) + ((c >>> 6) & 0x3)]);
            outStream.write(pem_array[c & 0x3F]);
        }
    }
}