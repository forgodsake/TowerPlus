/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */
         
// MESSAGE HIL_GPS PACKING
package com.MAVLink.common;
import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.ardupilotmega.CRC;
import java.nio.ByteBuffer;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
* The global position, as returned by the Global Positioning System (GPS). This is
                 NOT the global position estimate of the sytem, but rather a RAW sensor value. See message GLOBAL_POSITION for the global position estimate. Coordinate frame is right-handed, Z-axis up (GPS frame).
*/
public class msg_hil_gps_test{

public static final int MAVLINK_MSG_ID_HIL_GPS = 113;
public static final int MAVLINK_MSG_LENGTH = 36;
private static final long serialVersionUID = MAVLINK_MSG_ID_HIL_GPS;

private Parser parser = new Parser();

public CRC generateCRC(byte[] packet){
    CRC crc = new CRC();
    for (int i = 1; i < packet.length - 2; i++) {
        crc.update_checksum(packet[i] & 0xFF);
    }
    crc.finish_checksum(MAVLINK_MSG_ID_HIL_GPS);
    return crc;
}

public byte[] generateTestPacket(){
    ByteBuffer payload = ByteBuffer.allocate(6 + MAVLINK_MSG_LENGTH + 2);
    payload.put((byte)MAVLinkPacket.MAVLINK_STX); //stx
    payload.put((byte)MAVLINK_MSG_LENGTH); //len
    payload.put((byte)0); //seq
    payload.put((byte)255); //sysid
    payload.put((byte)190); //comp id
    payload.put((byte)MAVLINK_MSG_ID_HIL_GPS); //msg id
    payload.putLong(93372036854775807L); //time_usec
    payload.putInt(963497880); //lat
    payload.putInt(963498088); //lon
    payload.putInt(963498296); //alt
    payload.putShort((short)18275); //eph
    payload.putShort((short)18379); //epv
    payload.putShort((short)18483); //vel
    payload.putShort((short)18587); //vn
    payload.putShort((short)18691); //ve
    payload.putShort((short)18795); //vd
    payload.putShort((short)18899); //cog
    payload.put((byte)235); //fix_type
    payload.put((byte)46); //satellites_visible
    
    CRC crc = generateCRC(payload.array());
    payload.put((byte)crc.getLSB());
    payload.put((byte)crc.getMSB());
    return payload.array();
}

@Test
public void test(){
    byte[] packet = generateTestPacket();
    for(int i = 0; i < packet.length - 1; i++){
        parser.mavlink_parse_char(packet[i] & 0xFF);
    }
    MAVLinkPacket m = parser.mavlink_parse_char(packet[packet.length - 1] & 0xFF);
    byte[] processedPacket = m.encodePacket();
    assertArrayEquals("msg_hil_gps", processedPacket, packet);
}
}
        