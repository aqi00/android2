package com.example.device.nfc;

import android.annotation.SuppressLint;
import android.nfc.tech.IsoDep;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressLint("DefaultLocale")
public class BusCard {
    private final static String TAG = "BusCard";
    private final static int SFI_EXTRA_LOG = 4;
    private final static int SFI_EXTRA_CNT = 5;
    private final static byte[] DFI_MF = {(byte) 0x3F, (byte) 0x00};
    private final static byte[] DFI_EP = {(byte) 0x10, (byte) 0x01};
    private final static byte[] DFN_PSE = {(byte) '1', (byte) 'P',
            (byte) 'A', (byte) 'Y', (byte) '.', (byte) 'S', (byte) 'Y',
            (byte) 'S', (byte) '.', (byte) 'D', (byte) 'D', (byte) 'F',
            (byte) '0', (byte) '1',};
    private final static byte[] DFN_PXX = {(byte) 'P'};
    private final static int MAX_LOG = 10;
    private final static int SFI_EXTRA = 21;
    private final static int SFI_LOG = 24;
    private final static byte TRANS_CSU = 6;
    private final static byte TRANS_CSU_CPX = 9;

    private String mID;
    private String mSerial;
    private String mVersion;
    private String mValidDate;
    private String mUsedCount;
    private String mBalance;
    private String mConsumeLog;

    public static String parser(IsoDep tech) {
        Iso7816.Tag tag = new Iso7816.Tag(tech);
        tag.connect();
        BusCard card = BusCard.loadCard(tag);
        tag.close();
        return (card != null) ? card.formatInfo() : "";
    }

    private BusCard(Iso7816.Tag tag) {
        mID = tag.getID().toString();
    }

    private static BusCard loadCard(Iso7816.Tag tag) {
        // select PSF (1PAY.SYS.DDF01)
        if (tag.selectByName(DFN_PSE).isOkey()) {
            Iso7816.Response INFO, CNT, CASH;
            // read card info file, binary (4)
            INFO = tag.readBinary(SFI_EXTRA_LOG);
            if (INFO.isOkey()) {
                // read card operation file, binary (5)
                CNT = tag.readBinary(SFI_EXTRA_CNT);
                // select Main Application
                if (tag.selectByID(DFI_EP).isOkey()) {
                    // read balance
                    CASH = tag.getBalance(true);
                    // read log file, record (24)
                    ArrayList<byte[]> LOG = readLog(tag, SFI_LOG);
                    // build result string
                    BusCard card = new BusCard(tag);
                    card.parseBalance(CASH);
                    card.parseInfo(INFO, CNT);
                    card.parseLog(LOG);
                    return card;
                }
            }
        }
        return null;
    }

    private void parseInfo(Iso7816.Response info, Iso7816.Response cnt) {
        if (!info.isOkey() || info.size() < 32) {
            return;
        }
        final byte[] d = info.getBytes();
        mSerial = NfcUtil.toHexString(d, 0, 8);
        mVersion = String.format("%02X.%02X%02X", d[8], d[9], d[10]);
        mValidDate = String.format("%02X%02X.%02X.%02X - %02X%02X.%02X.%02X",
                d[24], d[25], d[26], d[27], d[28], d[29], d[30], d[31]);
        if (cnt != null && cnt.isOkey() && cnt.size() > 4) {
            byte[] e = cnt.getBytes();
            final int n = NfcUtil.toInt(e, 1, 4);
            if (e[0] == 0) {
                mUsedCount = String.format("%d", n);
            } else {
                mUsedCount = String.format("%d*", n);
            }
        }
    }

    private static boolean addLog(final Iso7816.Response r, ArrayList<byte[]> l) {
        if (!r.isOkey()) {
            return false;
        }
        byte[] raw = r.getBytes();
        int N = raw.length - 23;
        if (N < 0) {
            return false;
        }
        for (int s = 0, e = 0; s <= N; s = e) {
            l.add(Arrays.copyOfRange(raw, s, (e = s + 23)));
        }
        return true;
    }

    private static ArrayList<byte[]> readLog(Iso7816.Tag tag, int sfi) {
        ArrayList<byte[]> ret = new ArrayList<byte[]>(MAX_LOG);
        Iso7816.Response resp = tag.readRecord(sfi);
        if (resp.isOkey()) {
            addLog(resp, ret);
        } else {
            for (int i = 1; i <= MAX_LOG; ++i) {
                if (!addLog(tag.readRecord(sfi, i), ret)) {
                    break;
                }
            }
        }
        return ret;
    }

    private void parseLog(ArrayList<byte[]>... logs) {
        String desc = "";
        for (ArrayList<byte[]> log : logs) {
            if (log == null) {
                continue;
            }
            for (byte[] v : log) {
                int cash = NfcUtil.toInt(v, 5, 4);
                if (cash > 0) {
                    desc = String.format("%s\n%02X%02X-%02X-%02X %02X:%02X:%02X %s了%s元",
                            desc, v[16], v[17], v[18], v[19], v[20], v[21], v[22],
                            (v[9] == TRANS_CSU || v[9] == TRANS_CSU_CPX) ? "消费" : "充值",
                            NfcUtil.toAmountString(cash / 100.0f));
                }
            }
        }
        mConsumeLog = desc;
    }

    private void parseBalance(Iso7816.Response data) {
        if (!data.isOkey() || data.size() < 4) {
            mBalance = null;
            return;
        }
        int n = NfcUtil.toInt(data.getBytes(), 0, 4);
        if (n > 100000 || n < -100000) {
            n -= 0x80000000;
        }
        mBalance = NfcUtil.toAmountString(n / 100.0f);
    }

    private String formatInfo() {
        return String.format("\t卡内余额：%s元\n\t序列号：%s\n\t版本号：%s\n\t有效期：%s\n\t一共使用了%s次\n\t刷卡记录如下：%s",
                mBalance, mSerial, mVersion, mValidDate, mUsedCount, mConsumeLog);
    }

}
