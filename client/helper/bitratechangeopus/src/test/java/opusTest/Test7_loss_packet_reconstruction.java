package opusTest;

import com.reve.audio.libraries.dlls.codec.opus.OPUSDecoder;
import com.reve.audio.libraries.dlls.codec.opus.OPUSEncoder;
import com.reve.util.ShortArrayUtil;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;

public class Test7_loss_packet_reconstruction {

    @Test
    void applyFilters() throws IOException {

        int sampleRate = 48000;
        int encoderBitRate = 24000;
        int decoderBitRate = 48000;

        int packetDropPercentage = 20;
        int processingTimeMs = 10 * 2;
        int channel = 1;
        int chunkSizeShort = (sampleRate * processingTimeMs * channel) / 1000;

        OPUSEncoder opusEncoder = new OPUSEncoder(sampleRate, encoderBitRate, processingTimeMs);
//        opusEncoder.enableFEC(packetDropPercentage);
        OPUSDecoder opusDecoder = new OPUSDecoder(sampleRate, decoderBitRate, processingTimeMs);

        File micFile = new File("F:\\audio\\opus_bitrate_vs_loss_percentage_test\\input\\jakir_48khz.raw");
        FileInputStream micFis = new FileInputStream(micFile);

        String outputFolder = "F:\\audio\\opus_bitrate_vs_loss_percentage_test\\output\\audio\\";
        FileOutputStream fos = new FileOutputStream(new File(outputFolder + "noFEC_0_recover_" + packetDropPercentage + ".raw"));

        String csvFilePath = "F:\\audio\\opus_bitrate_vs_loss_percentage_test\\output\\csv\\" + "jakir_48khzlossPercentage_" + packetDropPercentage + ".csv";
        FileWriter csvWriter = new FileWriter(csvFilePath);
        csvWriter.append("encoded_data_length\n");

        byte[] micByte = new byte[chunkSizeShort * 2];
        int chunk_size = chunkSizeShort * 2;
        int micReadLength = 0;

        short[] encodeShortBuffer = new short[chunkSizeShort];
        short[] decodeShortBufferTemp = new short[chunkSizeShort];
        short[] decodedShortBuffer = new short[chunkSizeShort];

        byte[] tempByteArray = new byte[2048];
        byte[] tempByteArray1 = new byte[2048];
        byte[] emptyData = new byte[chunkSizeShort * 2];


        int shortLen = 0;
        int byteLen = 0;
        int encodeDataLength = 0;
        int decodeDataLength = 0;

        long t1 = System.currentTimeMillis();
        System.out.println("entering loop : "+t1);
        int i = 0;
        int packetLossSequence = (100/packetDropPercentage) * 2;

        while(true){
            micReadLength = micFis.read(micByte, 0, chunk_size);

            if(micReadLength < chunk_size){
                System.out.println("breaking");
                break;
            }


            shortLen = ShortArrayUtil.convertByteToShort(micByte, 0, micReadLength, encodeShortBuffer, 0);
            encodeDataLength = opusEncoder.encode(encodeShortBuffer, 0, shortLen, tempByteArray, 0);
//            csvWriter.append(Integer.toString(encodeDataLength) + "\n");
//            System.out.println(i + " encodedDataLength=" + encodeDataLength);


            if (i < 10) {
                decodeDataLength = opusDecoder.decode(tempByteArray, 0, encodeDataLength, decodedShortBuffer, 0);

                byteLen = ShortArrayUtil.convertShortToByte(decodedShortBuffer, 0, decodeDataLength, tempByteArray, 0);
                try {
                    fos.write(tempByteArray, 0, byteLen);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                if(i != 0 && (i % packetLossSequence == 0 || i % packetLossSequence == 1)){
                    // simulate loss by doing nothing
                    i++;
                    continue;
                } else if (i != 1 && i % packetLossSequence == 2) { // trying to recover previous lost packet

                    try {
                        fos.write(emptyData, 0, byteLen);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Arrays.fill(decodeShortBufferTemp,(short)0);
                    int lostPacketLen = chunkSizeShort;
//                    lostPacketLen = opusDecoder.decodeLostPacket(tempByteArray, 0, encodeDataLength, decodeShortBufferTemp, 0);
                    byteLen = ShortArrayUtil.convertShortToByte(decodeShortBufferTemp, 0, lostPacketLen, tempByteArray1, 0);
                    try {
                        fos.write(tempByteArray1, 0, byteLen);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                // decode
                decodeDataLength = opusDecoder.decode(tempByteArray, 0, encodeDataLength, decodedShortBuffer, 0);

                byteLen = ShortArrayUtil.convertShortToByte(decodedShortBuffer, 0, decodeDataLength, tempByteArray, 0);
                try {
                    fos.write(tempByteArray, 0, byteLen);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            i++;
        }

        long t2 = System.currentTimeMillis();
        System.out.println("time taken : "+(t2-t1)+" ms");
        micFis.close();
        fos.close();
        csvWriter.close();
    }
}
