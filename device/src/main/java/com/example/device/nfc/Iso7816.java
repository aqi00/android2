package com.example.device.nfc;

import android.nfc.tech.IsoDep;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Iso7816 {
    private static final byte[] EMPTY = {0};
    public static final short SW_NO_ERROR = (short) 0x9000;
    protected byte[] data;

    public Iso7816(byte[] bytes) {
        data = (bytes == null) ? Iso7816.EMPTY : bytes;
    }

    public boolean match(byte[] bytes, int start) {
        final byte[] data = this.data;
        if (data.length <= bytes.length - start) {
            for (final byte v : data) {
                if (v != bytes[start++]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int size() {
        return data.length;
    }

    public byte[] getBytes() {
        return data;
    }

    public String toString() {
        return NfcUtil.toHexString(data, 0, data.length);
    }

    public final static class ID extends Iso7816 {
        public ID(byte[] bytes) {
            super(bytes);
        }
    }

    public final static class Response extends Iso7816 {
        public static final byte[] EMPTY = {};
        public static final byte[] ERROR = {0x6F, 0x00}; // SW_UNKNOWN

        public Response(byte[] bytes) {
            super((bytes == null || bytes.length < 2) ? Response.ERROR : bytes);
        }

        public byte getSw1() {
            return data[data.length - 2];
        }

        public byte getSw2() {
            return data[data.length - 1];
        }

        public short getSw12() {
            final byte[] d = this.data;
            int n = d.length;
            return (short) ((d[n - 2] << 8) | (0xFF & d[n - 1]));
        }

        public boolean isOkey() {
            return equalsSw12(SW_NO_ERROR);
        }

        public boolean equalsSw12(short val) {
            return getSw12() == val;
        }

        public int size() {
            return data.length - 2;
        }

        public byte[] getBytes() {
            return isOkey() ? Arrays.copyOfRange(data, 0, size()) : Response.EMPTY;
        }
    }

    public final static class Tag {
        private final IsoDep nfcTag;
        private ID id;

        public Tag(IsoDep tag) {
            nfcTag = tag;
            id = new ID(tag.getTag().getId());
        }

        public ID getID() {
            return id;
        }

        public Response verify() {
            final byte[] cmd = {(byte) 0x00, // CLA Class
                    (byte) 0x20, // INS Instruction
                    (byte) 0x00, // P1 Parameter 1
                    (byte) 0x00, // P2 Parameter 2
                    (byte) 0x02, // Lc
                    (byte) 0x12, (byte) 0x34,};
            return new Response(transceive(cmd));
        }

        public Response initPurchase(boolean isEP) {
            final byte[] cmd = {
                    (byte) 0x80, // CLA Class
                    (byte) 0x50, // INS Instruction
                    (byte) 0x01, // P1 Parameter 1
                    (byte) (isEP ? 2 : 1), // P2 Parameter 2
                    (byte) 0x0B, // Lc
                    (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x11, (byte) 0x22, (byte) 0x33,
                    (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x0F, // Le
            };
            return new Response(transceive(cmd));
        }

        public Response getBalance(boolean isEP) {
            final byte[] cmd = {(byte) 0x80, // CLA Class
                    (byte) 0x5C, // INS Instruction
                    (byte) 0x00, // P1 Parameter 1
                    (byte) (isEP ? 2 : 1), // P2 Parameter 2
                    (byte) 0x04, // Le
            };
            return new Response(transceive(cmd));
        }

        public Response readRecord(int sfi, int index) {
            final byte[] cmd = {(byte) 0x00, // CLA Class
                    (byte) 0xB2, // INS Instruction
                    (byte) index, // P1 Parameter 1
                    (byte) ((sfi << 3) | 0x04), // P2 Parameter 2
                    (byte) 0x00, // Le
            };
            return new Response(transceive(cmd));
        }

        public Response readRecord(int sfi) {
            final byte[] cmd = {(byte) 0x00, // CLA Class
                    (byte) 0xB2, // INS Instruction
                    (byte) 0x01, // P1 Parameter 1
                    (byte) ((sfi << 3) | 0x05), // P2 Parameter 2
                    (byte) 0x00, // Le
            };
            return new Response(transceive(cmd));
        }

        public Response readBinary(int sfi) {
            final byte[] cmd = {(byte) 0x00, // CLA Class
                    (byte) 0xB0, // INS Instruction
                    (byte) (0x00000080 | (sfi & 0x1F)), // P1 Parameter 1
                    (byte) 0x00, // P2 Parameter 2
                    (byte) 0x00, // Le
            };
            return new Response(transceive(cmd));
        }

        public Response readData(int sfi) {
            final byte[] cmd = {(byte) 0x80, // CLA Class
                    (byte) 0xCA, // INS Instruction
                    (byte) 0x00, // P1 Parameter 1
                    (byte) (sfi & 0x1F), // P2 Parameter 2
                    (byte) 0x00, // Le
            };
            return new Response(transceive(cmd));
        }

        public Response selectByID(byte... name) {
            ByteBuffer buff = ByteBuffer.allocate(name.length + 6);
            buff.put((byte) 0x00) // CLA Class
                    .put((byte) 0xA4) // INS Instruction
                    .put((byte) 0x00) // P1 Parameter 1
                    .put((byte) 0x00) // P2 Parameter 2
                    .put((byte) name.length) // Lc
                    .put(name).put((byte) 0x00); // Le
            return new Response(transceive(buff.array()));
        }

        public Response selectByName(byte... name) {
            ByteBuffer buff = ByteBuffer.allocate(name.length + 6);
            buff.put((byte) 0x00) // CLA Class
                    .put((byte) 0xA4) // INS Instruction
                    .put((byte) 0x04) // P1 Parameter 1
                    .put((byte) 0x00) // P2 Parameter 2
                    .put((byte) name.length) // Lc
                    .put(name).put((byte) 0x00); // Le
            return new Response(transceive(buff.array()));
        }

        public void connect() {
            try {
                nfcTag.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                nfcTag.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public byte[] transceive(final byte[] cmd) {
            try {
                return nfcTag.transceive(cmd);
            } catch (Exception e) {
                e.printStackTrace();
                return Response.ERROR;
            }
        }
    }

}
