/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */
         
// MESSAGE HIL_SENSOR PACKING
package com.MAVLink.common;
import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.ardupilotmega.CRC;
import java.nio.ByteBuffer;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
* The IMU readings in SI units in NED body frame
*/
public class msg_hil_sensor_test{

public static final int MAVLINK_MSG_ID_HIL_SENSOR = 107;
public static final int MAVLINK_MSG_LENGTH = 64;
private static final long serialVersionUID = MAVLINK_MSG_ID_HIL_SENSOR;

private Parser parser = new Parser();

public CRC generateCRC(byte[] packet){
    CRC crc = new CRC();
    for (int i = 1; i < packet.length - 2; i++) {
        crc.update_checksum(packet[i] & 0xFF);
    }
    crc.finish_checksum(MAVLINK_MSG_ID_HIL_SENSOR);
    return crc;
}

public byte[] generateTestPacket(){
    ByteBuffer payload = ByteBuffer.allocate(6 + MAVLINK_MSG_LENGTH + 2);
    payload.put((byte)MAVLinkPacket.MAVLINK_STX); //stx
    payload.put((byte)MAVLINK_MSG_LENGTH); //len
    payload.put((byte)0); //seq
    payload.put((byte)255); //sysid
    payload.put((byte)190); //comp id
    payload.put((byte)MAVLINK_MSG_ID_HIL_SENSOR); //msg id
    payload.putLong(93372036854775807L); //time_usec
    payload.putFloat((float)73.0); //xacc
    payload.putFloat((float)101.0); //yacc
    payload.putFloat((float)129.0); //zacc
    payload.putFloat((float)157.0); //xgyro
    payload.putFloat((float)185.0); //ygyro
    payload.putFloat((float)213.0); //zgyro
    payload.putFloat((float)241.0); //xmag
    payload.putFloat((float)269.0); //ymag
    payload.putFloat((float)297.0); //zmag
    payload.putFloat((float)325.0); //abs_pressure
    payload.putFloat((float)353.0); //diff_pressure
    payload.putFloat((float)381.0); //pressure_alt
    payload.putFloat((float)409.0); //temperature
    payload.putInt(963500584); //fields_updated
    
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
    assertArrayEquals("msg_hil_sensor", processedPacket, packet);
}
}
        