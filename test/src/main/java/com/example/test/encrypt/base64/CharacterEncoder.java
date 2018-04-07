/*
    2    * Copyright (c) 1995, 2005, Oracle and/or its affiliates. All rights reserved.
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

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 38 * This class defines the encoding half of character encoders. 39 * A
 * character encoder is an algorithim for transforming 8 bit binary 40 * data
 * into text (generally 7 bit ASCII or 8 bit ISO-Latin-1 text) 41 * for
 * transmition over text channels such as e-mail and network news. 42 * 43 * The
 * character encoders have been structured around a central theme 44 * that, in
 * general, the encoded text has the form: 45 * 46 *
 * <p>
 * <pre>
 *    47    *      [Buffer Prefix]
 *    48    *      [Line Prefix][encoded data atoms][Line Suffix]
 *    49    *      [Buffer Suffix]
 *    50    *
 * </pre>
 * <p>
 * 51 * 52 * In the CharacterEncoder and CharacterDecoder classes, one complete
 * 53 * chunk of data is referred to as a <i>buffer</i>. Encoded buffers 54 *
 * are all text, and decoded buffers (sometimes just referred to as 55 *
 * buffers) are binary octets. 56 * 57 * To create a custom encoder, you must,
 * at a minimum, overide three 58 * abstract methods in this class. 59 *
 * <DL>
 * 60 *
 * <DD>bytesPerAtom which tells the encoder how many bytes to 61 * send to
 * encodeAtom 62 *
 * <DD>encodeAtom which encodes the bytes sent to it as text. 63 *
 * <DD>bytesPerLine which tells the encoder the maximum number of 64 * bytes per
 * line. 65 *
 * </DL>
 * 66 * 67 * Several useful encoders have already been written and are 68 *
 * referenced in the See Also list below. 69 * 70 * @author Chuck McManis 71 * @see
 * CharacterDecoder; 72 * @see UCEncoder 73 * @see UUEncoder 74 * @see
 * BASE64Encoder 75
 */
public abstract class CharacterEncoder {
    /**
     * Stream that understands "printing"
     */
    protected PrintStream pStream;

    /**
     * Return the number of bytes per atom of encoding
     */
    abstract protected int bytesPerAtom();

    /**
     * Return the number of bytes that can be encoded per line
     */
    abstract protected int bytesPerLine();

    /**
     * 88 * Encode the prefix for the entire buffer. By default is simply 89 *
     * opens the PrintStream for use by the other functions. 90
     */
    protected void encodeBufferPrefix(OutputStream aStream) throws IOException {
        pStream = new PrintStream(aStream);
    }

    /**
     * 96 * Encode the suffix for the entire buffer. 97
     */
    protected void encodeBufferSuffix(OutputStream aStream) throws IOException {
    }

    /**
     * 102 * Encode the prefix that starts every output line. 103
     */
    protected void encodeLinePrefix(OutputStream aStream, int aLength)
            throws IOException {
    }

    /**
     * 109 * Encode the suffix that ends every output line. By default 110 *
     * this method just prints a <newline> into the output stream. 111
     */
    protected void encodeLineSuffix(OutputStream aStream) throws IOException {
        pStream.println();
    }

    /**
     * Encode one "atom" of information into characters.
     */
    abstract protected void encodeAtom(OutputStream aStream, byte someBytes[],
                                       int anOffset, int aLength) throws IOException;

    /**
     * 121 * This method works around the bizarre semantics of
     * BufferedInputStream's 122 * read method. 123
     */
    protected int readFully(InputStream in, byte buffer[])
            throws java.io.IOException {
        for (int i = 0; i < buffer.length; i++) {
            int q = in.read();
            if (q == -1)
                return i;
            buffer[i] = (byte) q;
        }
        return buffer.length;
    }

    /**
     * 136 * Encode bytes from the input stream, and write them as text
     * characters 137 * to the output stream. This method will run until it
     * exhausts the 138 * input stream, but does not print the line suffix for a
     * final 139 * line that is shorter than bytesPerLine(). 140
     */
    public void encode(InputStream inStream, OutputStream outStream)
            throws IOException {
        int j;
        int numBytes;
        byte tmpbuffer[] = new byte[bytesPerLine()];

        encodeBufferPrefix(outStream);

        while (true) {
            numBytes = readFully(inStream, tmpbuffer);
            if (numBytes == 0) {
                break;
            }
            encodeLinePrefix(outStream, numBytes);
            for (j = 0; j < numBytes; j += bytesPerAtom()) {

                if ((j + bytesPerAtom()) <= numBytes) {
                    encodeAtom(outStream, tmpbuffer, j, bytesPerAtom());
                } else {
                    encodeAtom(outStream, tmpbuffer, j, (numBytes) - j);
                }
            }
            if (numBytes < bytesPerLine()) {
                break;
            } else {
                encodeLineSuffix(outStream);
            }
        }
        encodeBufferSuffix(outStream);
    }

    /**
     * 173 * Encode the buffer in <i>aBuffer</i> and write the encoded 174 *
     * result to the OutputStream <i>aStream</i>. 175
     */
    public void encode(byte aBuffer[], OutputStream aStream) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(aBuffer);
        encode(inStream, aStream);
    }

    /**
     * 183 * A 'streamless' version of encode that simply takes a buffer of 184
     * * bytes and returns a string containing the encoded buffer. 185
     */
    public String encode(byte aBuffer[]) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayInputStream inStream = new ByteArrayInputStream(aBuffer);
        String retVal = null;
        try {
            encode(inStream, outStream);
            // explicit ascii->unicode conversion
            retVal = outStream.toString("8859_1");
        } catch (Exception IOException) {
            // This should never happen.
            throw new Error("CharacterEncoder.encode internal error");
        }
        return (retVal);
    }

    /**
     * 202 * Return a byte array from the remaining bytes in this ByteBuffer.
     * 203 *
     * <p>
     * 204 * The ByteBuffer's position will be advanced to ByteBuffer's limit.
     * 205 *
     * <p>
     * 206 * To avoid an extra copy, the implementation will attempt to return
     * the 207 * byte array backing the ByteBuffer. If this is not possible, a
     * 208 * new byte array will be created. 209
     */
    private byte[] getBytes(ByteBuffer bb) {
        /*
		 * This should never return a BufferOverflowException, as we're 213 *
		 * careful to allocate just the right amount. 214
		 */
        byte[] buf = null;

		/*
		 * 218 * If it has a usable backing byte buffer, use it. Use only 219 *
		 * if the array exactly represents the current ByteBuffer. 220
		 */
        if (bb.hasArray()) {
            byte[] tmp = bb.array();
            if ((tmp.length == bb.capacity()) && (tmp.length == bb.remaining())) {
                buf = tmp;
                bb.position(bb.limit());
            }
        }

        if (buf == null) {
			/*
			 * 232 * This class doesn't have a concept of encode(buf, len, off),
			 * 233 * so if we have a partial buffer, we must reallocate 234 *
			 * space. 235
			 */
            buf = new byte[bb.remaining()];

			/*
			 * 239 * position() automatically updated 240
			 */
            bb.get(buf);
        }

        return buf;
    }

    /**
     * 248 * Encode the <i>aBuffer</i> ByteBuffer and write the encoded 249 *
     * result to the OutputStream <i>aStream</i>. 250 *
     * <p>
     * 251 * The ByteBuffer's position will be advanced to ByteBuffer's limit.
     * 252
     */
    public void encode(ByteBuffer aBuffer, OutputStream aStream)
            throws IOException {
        byte[] buf = getBytes(aBuffer);
        encode(buf, aStream);
    }

    /**
     * 260 * A 'streamless' version of encode that simply takes a ByteBuffer 261
     * * and returns a string containing the encoded buffer. 262 *
     * <p>
     * 263 * The ByteBuffer's position will be advanced to ByteBuffer's limit.
     * 264
     */
    public String encode(ByteBuffer aBuffer) {
        byte[] buf = getBytes(aBuffer);
        return encode(buf);
    }

    /**
     * 271 * Encode bytes from the input stream, and write them as text
     * characters 272 * to the output stream. This method will run until it
     * exhausts the 273 * input stream. It differs from encode in that it will
     * add the 274 * line at the end of a final line that is shorter than
     * bytesPerLine(). 275
     */
    public void encodeBuffer(InputStream inStream, OutputStream outStream)
            throws IOException {
        int j;
        int numBytes;
        byte tmpbuffer[] = new byte[bytesPerLine()];

        encodeBufferPrefix(outStream);

        while (true) {
            numBytes = readFully(inStream, tmpbuffer);
            if (numBytes == 0) {
                break;
            }
            encodeLinePrefix(outStream, numBytes);
            for (j = 0; j < numBytes; j += bytesPerAtom()) {
                if ((j + bytesPerAtom()) <= numBytes) {
                    encodeAtom(outStream, tmpbuffer, j, bytesPerAtom());
                } else {
                    encodeAtom(outStream, tmpbuffer, j, (numBytes) - j);
                }
            }
            encodeLineSuffix(outStream);
            if (numBytes < bytesPerLine()) {
                break;
            }
        }
        encodeBufferSuffix(outStream);
    }

    /**
     * 306 * Encode the buffer in <i>aBuffer</i> and write the encoded 307 *
     * result to the OutputStream <i>aStream</i>. 308
     */
    public void encodeBuffer(byte aBuffer[], OutputStream aStream)
            throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(aBuffer);
        encodeBuffer(inStream, aStream);
    }

    /**
     * 316 * A 'streamless' version of encode that simply takes a buffer of 317
     * * bytes and returns a string containing the encoded buffer. 318
     */
    public String encodeBuffer(byte aBuffer[]) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayInputStream inStream = new ByteArrayInputStream(aBuffer);
        try {
            encodeBuffer(inStream, outStream);
        } catch (Exception IOException) {
            // This should never happen.
            throw new Error("CharacterEncoder.encodeBuffer internal error");
        }
        return (outStream.toString());
    }

    /**
     * 332 * Encode the <i>aBuffer</i> ByteBuffer and write the encoded 333 *
     * result to the OutputStream <i>aStream</i>. 334 *
     * <p>
     * 335 * The ByteBuffer's position will be advanced to ByteBuffer's limit.
     * 336
     */
    public void encodeBuffer(ByteBuffer aBuffer, OutputStream aStream)
            throws IOException {
        byte[] buf = getBytes(aBuffer);
        encodeBuffer(buf, aStream);
    }

    /**
     * 344 * A 'streamless' version of encode that simply takes a ByteBuffer 345
     * * and returns a string containing the encoded buffer. 346 *
     * <p>
     * 347 * The ByteBuffer's position will be advanced to ByteBuffer's limit.
     * 348
     */
    public String encodeBuffer(ByteBuffer aBuffer) {
        byte[] buf = getBytes(aBuffer);
        return encodeBuffer(buf);
    }

}