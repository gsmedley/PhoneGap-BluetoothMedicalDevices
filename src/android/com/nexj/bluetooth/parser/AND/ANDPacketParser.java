package com.nexj.bluetooth.parser.AND;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.util.Log;

import com.nexj.bluetooth.parser.PacketParser;
import com.nexj.bluetooth.util.DeviceMismatchException;

public class ANDPacketParser extends PacketParser<ANDPacket, ANDHeader>
{
    protected static final byte[] DATA_ACCEPT = {'P', 'W', 'A', '1'}; // command to acknowledge receipt of data
    protected static final byte[] ENTER_CONFIGURATION = {'P', 'W', 'A', '2'}; // command to tell device we want to reset date/time
    protected static final byte[] CON_REQ_PACKET = {'P', 'W', 'R', 'Q', 'C', 'F'};
    protected static final byte[] CHANGE_SUCCESSFUL = {'P', 'W', 'C', '1'};
    protected static final int HEADER_PACKET_SIZE = 60;
    protected static final int BLOOD_PRESSURE_PACKET_SIZE = 10;
    protected static final int WEIGHT_SCALE_PACKET_SIZE = 14;

    protected static final String[] SUPPORTED_DEVICE_TYPES;

    protected int m_nExpectedDevicePacketSize = 0;

    static
    {
        SUPPORTED_DEVICE_TYPES = new String[]{"weight", "blood pressure"};
    }

    public ANDPacketParser()
    {
        super(ANDPacketParser.class.getName());
    }

    protected void preHeader(InputStream is, OutputStream os, String sDeviceType) throws IOException
    {
        super.preHeader(is, os, sDeviceType);

        if ("weight".equals(sDeviceType))
        {
            m_nExpectedDevicePacketSize = WEIGHT_SCALE_PACKET_SIZE;
        }
        else if ("blood pressure".equals(sDeviceType))
        {
            m_nExpectedDevicePacketSize = BLOOD_PRESSURE_PACKET_SIZE;
        }
        else
        {
            m_nExpectedDevicePacketSize = 0;
        }
    }

    protected void postHeader(InputStream is, OutputStream os, ANDHeader header) throws IOException
    {
        super.postHeader(is, os, header);

        if (m_nExpectedDevicePacketSize != 0 && m_nExpectedDevicePacketSize != header.m_nPacketLength)
        {
            throw new DeviceMismatchException("Device type did not match expected");
        }
    }

    protected void postBody(InputStream is, OutputStream os, ANDPacket packet) throws IOException
    {
        super.postBody(is, os, packet);

        try
        {
            respond(packet.m_header, is, os);
        }
        catch (IOException ignored)
        {
            Log.e(m_sTag, "Error responding to medical device", ignored);
        }
    }

    protected void respond(ANDHeader header, InputStream is, OutputStream os) throws IOException
    {
        ByteArrayOutputStream bOs = new ByteArrayOutputStream();

        Calendar now = Calendar.getInstance(); // get the time for comparison purposes
        int nYear = now.get(Calendar.YEAR);
        int nMonth = now.get(Calendar.MONTH) + 1;
        int nDay = now.get(Calendar.DAY_OF_MONTH);
        int nHour = now.get(Calendar.HOUR_OF_DAY);
        int nMin = now.get(Calendar.MINUTE);

        /* Upon receiving the packet, the time on the device is checked for accuracy within 15 minutes exclusively */
        if (header.m_nYearOfTrans != nYear || header.m_nMonthOfTrans != nMonth || header.m_nDayOfTrans != nDay
                    || header.m_nHourOfTrans != nHour || header.m_nMinOfTrans - nMin > 15 || header.m_nMinOfTrans - nMin < -15)
        {
            // indicate to the device that we need to update the device time
            os.write(ENTER_CONFIGURATION);

            int count = read(bOs, is, 6);
            byte[] bBuffer = bOs.toByteArray();

            if (count == 6) // check that it's ready
            {
                if (bBuffer[0] == CON_REQ_PACKET[0] && bBuffer[1] == CON_REQ_PACKET[1] && bBuffer[2] == CON_REQ_PACKET[2]
                            && bBuffer[3] == CON_REQ_PACKET[3] && bBuffer[4] == CON_REQ_PACKET[4] && bBuffer[5] == CON_REQ_PACKET[5])
                {
                    byte[] bAck = new byte[82];

                    for (int i = 0; i < 82; ++i)
                    {
                        bAck[i] = 0;
                    }

                    bAck[62] = 1; // set clock

                    int nTop = nYear / 256;
                    int nBottom = nYear - nTop * 256;

                    bAck[63] = (byte)nBottom;
                    bAck[64] = (byte)nTop;
                    bAck[65] = (byte)nMonth;
                    bAck[66] = (byte)nDay;
                    bAck[67] = (byte)nHour;
                    bAck[68] = (byte)nMin;
                    bAck[69] = (byte)now.get(Calendar.SECOND);

                    os.write(bAck);

                    bOs.flush();

                    count = read(bOs, is, 4);
                    bBuffer = bOs.toByteArray();

                    if (count == 4)
                    {
                        if (bBuffer[0] == CHANGE_SUCCESSFUL[0] && bBuffer[1] == CHANGE_SUCCESSFUL[1]
                                    && bBuffer[2] == CHANGE_SUCCESSFUL[2] && bBuffer[3] == CHANGE_SUCCESSFUL[3])
                        {
                            Log.i(m_sTag, "Device date changed");
                        }
                        else
                        {
                            Log.i(m_sTag, "Device date not changed");
                        }
                    }
                }
            }
        }
        else
        {
            os.write(DATA_ACCEPT);
        }
    }

    protected Map<String, Object> parsePacket(ANDPacket packet) throws JSONException
    {
        ANDHeader header = packet.m_header;
        Calendar calendar = Calendar.getInstance();
        calendar.set(header.m_nYearOfMeas, header.m_nMonthOfMeas, header.m_nDayOfMeas, header.m_nHourOfMeas, header.m_nMinOfMeas, header.m_nSecOfMeas);

        HashMap<String, Object> payload = new HashMap<String, Object>(8);

        payload.put("time", calendar.getTimeInMillis() / 1000L);
        payload.put("serial", header.m_sSerialNmber);

        if (packet instanceof ANDBloodPressure)
        {
            ANDBloodPressure bloodPressure = (ANDBloodPressure)packet;

            payload.put("type", "blood pressure");
            payload.put("systolic", bloodPressure.m_nSysDia);
            payload.put("diastolic", bloodPressure.m_nDia);
            payload.put("pulse", bloodPressure.m_nPulse);
            payload.put("mean", bloodPressure.m_nMAP);
        }
        else if (packet instanceof ANDWeight)
        {
            ANDWeight weight = (ANDWeight)packet;

            payload.put("type", "weight");
            payload.put("weight", weight.m_nWeight);
            payload.put("system", weight.m_sLbsKgs);
        }

        return payload;
    }

    protected ANDPacket createPacket(ANDHeader header, InputStream is) throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int nLength = read(os, is, header.m_nPacketLength);

        ANDPacket packet = null;

        if (nLength != header.m_nPacketLength)
        {
            throw new IOException("Expected packet length: " + header.m_nPacketLength + ", did not match read packet length: " + nLength);
        }
        else if (nLength == WEIGHT_SCALE_PACKET_SIZE)
        {
            packet = createWeightPacket(header, os.toByteArray());
        }
        else if (nLength == BLOOD_PRESSURE_PACKET_SIZE)
        {
            packet = createBloodPressurePacket(header, os.toByteArray());
        }
        else
        {
            throw new IOException("Unsupported packet type, length: " + nLength);
        }

        return packet;
    }

    protected int read(ByteArrayOutputStream os, InputStream is, int len) throws IOException
    {
        super.read(os, is, len);

        return os.size();
    }

    protected ANDWeight createWeightPacket(ANDHeader header, byte[] bBuffer)
    {
        ANDWeight weightPacket = new ANDWeight();  // Weight data unpack

        weightPacket.m_header = header;
        weightPacket.m_sLeadBytes = new String(bBuffer, 0, 3); // "ST." - 3 bytes

        try
        {
            weightPacket.m_nWeight = Float.parseFloat(new String(bBuffer, 3, 7)); // weight in either lbs or kgs depending on next field (7 bytes)
        }
        catch (Exception e)
        {
            Log.e(m_sTag, "Error parsing Weight: " + e.getMessage());
        }

        weightPacket.m_sLbsKgs = new String(bBuffer, 10, 2); // indicator of weight value
        weightPacket.m_bCarriageReturn = bBuffer[12]; // contains just a carriage return
        weightPacket.m_bLineFeed = bBuffer[13]; // contains just a line feed

        return weightPacket;
    }

    protected ANDBloodPressure createBloodPressurePacket(ANDHeader header, byte[] bBuffer)
    {
        ANDBloodPressure bloodPressurePacket = new ANDBloodPressure(); // Blood Pressure

        bloodPressurePacket.m_header = header;
        bloodPressurePacket.m_sValid = new String(bBuffer, 0, 2); // "80" is valid measurement - 2 Bytes

        if ("80".compareTo(bloodPressurePacket.m_sValid) == 0) // Reading Valid?
        {
            try
            {
                bloodPressurePacket.m_nSysDia = Integer.parseInt(new String(bBuffer, 2, 2), 16); // Systolic - Diastolic (2 Bytes)
            }
            catch (Exception e)
            {
                Log.e(m_sTag, "Error reading systolic: " + e.getMessage());
            }

            try
            {
                bloodPressurePacket.m_nDia = Integer.parseInt(new String(bBuffer, 4, 2), 16); // Diastolic - (2 Bytes)
            }
            catch (Exception e)
            {
                Log.e(m_sTag, "Error reading diastolic: " + e.getMessage());
            }

            bloodPressurePacket.m_nSysDia += bloodPressurePacket.m_nDia; // reconstitute m_nSysDia

            try
            {
                bloodPressurePacket.m_nPulse = Integer.parseInt(new String(bBuffer, 6, 2), 16); // Pulse Rate per minute (2 Bytes)
            }
            catch (Exception e)
            {
                Log.e(m_sTag, "Error reading pulse: " + e.getMessage());
            }

            try
            {
                bloodPressurePacket.m_nMAP = Integer.parseInt(new String(bBuffer, 8, 2), 16); // Mean Arterial Pressure (2 Bytes)
            }
            catch (Exception e)
            {
                Log.e(m_sTag, "Error reading arterial mean: " + e.getMessage());
            }
        }

        return bloodPressurePacket;
    }

    protected ANDHeader createHeader(InputStream is) throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        if (HEADER_PACKET_SIZE != read(os, is, HEADER_PACKET_SIZE))
        {
            throw new IOException("Expected header length: " + HEADER_PACKET_SIZE + ", did not match read header length: " + os.size());
        }

        byte[] bBuffer = os.toByteArray();

        ANDHeader header = new ANDHeader();

        header.m_nPacketType = bBuffer[1] * 16 + bBuffer[0];
        header.m_nPacketLength = bBuffer[5] * 16 * 16 * 16 + bBuffer[4] * 16 * 16 + bBuffer[3] * 16 + bBuffer[2];
        header.m_nDeviceType = bBuffer[7] * 16 + bBuffer[6];
        header.m_nFlag = bBuffer[8];
        header.m_nYearOfMeas = bBuffer[10] * 16 + bBuffer[9] + 1936;
        header.m_nMonthOfMeas = bBuffer[11] - 1;
        header.m_nDayOfMeas = bBuffer[12];
        header.m_nHourOfMeas = bBuffer[13];
        header.m_nMinOfMeas = bBuffer[14];
        header.m_nSecOfMeas = bBuffer[15];
        header.m_nYearOfTrans = bBuffer[17] * 16 + bBuffer[16] + 1936;
        header.m_nMonthOfTrans = bBuffer[18];
        header.m_nDayOfTrans = bBuffer[19];
        header.m_nHourOfTrans = bBuffer[20];
        header.m_nMinOfTrans = bBuffer[21];
        header.m_nSecOfTrans = bBuffer[22];
        header.m_sBluetoothId = new String(bBuffer, 23, 6);
        header.m_sAccessPointId = new String(bBuffer, 29, 6);
        header.m_sSerialNmber = new String(bBuffer, 35, 12).trim();
        header.m_bReserved1 = bBuffer[47];
        header.m_bReserved2 = bBuffer[48];
        header.m_bReserved3 = bBuffer[49];
        header.m_bReserved4 = bBuffer[50];
        header.m_bReserved5 = bBuffer[51];
        header.m_bReserved6 = bBuffer[52];
        header.m_bReserved7 = bBuffer[53];
        header.m_bReserved8 = bBuffer[54];
        header.m_bReserved9 = bBuffer[55];
        header.m_bReserved10 = bBuffer[56];
        header.m_nBatteryStatus = bBuffer[57];
        header.m_bReserved11 = bBuffer[58];
        header.m_bFirmware = bBuffer[59];

        return header;
    }

    public String[] getSupportedDeviceTypes()
    {
        return SUPPORTED_DEVICE_TYPES;
    }
}
