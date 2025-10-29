package opusTest;

import com.reve.audio.libraries.dlls.codec.opus.OPUSDecoder;
import com.reve.util.ShortArrayUtil;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;

public class Test8_DecodeWithFEC {

    @Test
    void applyFilters() throws IOException {

        int sampleRate = 48000;
        int bitRate = 48000;
        int processingTimeMs = 10 * 2;
        int channel = 1;
        int chunkSizeShort = (sampleRate * processingTimeMs * channel) / 1000;
        int recoverPacketCount = 1;

        String folder = "F:\\projects\\office_main_projects\\BitrateControlServer\\out\\artifacts\\BitrateControlServer_jar\\";
        String binaryFilePath = folder + "decoded_binary.bin";
        String csvFilePath = folder + "decoded_csv.csv";
        String decodedAudioFilePath = "F:\\audio\\bitrate_control_loss_based_test\\output\\FEC\\input_48k_FEC_5KBps_lowRange_10kbps_3.raw";

        FileInputStream fis_binary = new FileInputStream(binaryFilePath);
        FileOutputStream fos_decoded_audio = new FileOutputStream(decodedAudioFilePath);

        boolean firstTime = true;
        int lastSequenceNumber = 0;
        int sequenceNumber;
        int encodeBufferLen;
        int idx = 0;
        int index = 0;
        int count = 0;
        int lostPacketCount;

        byte[] encodedData = fis_binary.readAllBytes();
        byte[] tempByteArray = new byte[chunkSizeShort * 2];
        byte[] emptyData = new byte[chunkSizeShort * 2];
        short[] encodeShortBuffer = new short[chunkSizeShort];
        short[] decodeShortBufferTemp = new short[chunkSizeShort];

        BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFilePath));
        String row;

        OPUSDecoder opusDecoder = new OPUSDecoder(sampleRate, bitRate, processingTimeMs);
        Arrays.fill(emptyData, (byte)0);

        while ((row = bufferedReader.readLine()) != null) {
            if (idx == 0) {
                idx++;
                continue;
            }

            sequenceNumber = Integer.valueOf(row.split(",")[0]);
            encodeBufferLen = Integer.valueOf(row.split(",")[1]);

            byte[] encodeDataByteCopy = new byte[encodeBufferLen];
            System.arraycopy(encodedData, index, encodeDataByteCopy, 0, encodeBufferLen);
            index += encodeBufferLen;

            if (firstTime) {
                lastSequenceNumber = sequenceNumber;
                firstTime = false;
            }

            lostPacketCount = sequenceNumber - lastSequenceNumber - 1;
            if (lostPacketCount == recoverPacketCount) {
                // Decode lost Packet
                int lostPacketLen = opusDecoder.decodeLostPacket(encodeDataByteCopy, 0, encodeBufferLen, decodeShortBufferTemp, 0);
                int byteLen = ShortArrayUtil.convertShortToByte(decodeShortBufferTemp, 0, lostPacketLen, tempByteArray, 0);
                fos_decoded_audio.write(tempByteArray, 0, byteLen);
            } else if (lostPacketCount > recoverPacketCount) {

                count += lostPacketCount;
                for (int i = 0; i < lostPacketCount; i++) {
                    fos_decoded_audio.write(emptyData, 0, emptyData.length);
                }
            }

            int decodedDataLen = opusDecoder.decode(encodeDataByteCopy, 0, encodeBufferLen, encodeShortBuffer, 0);
            int byteLen = ShortArrayUtil.convertShortToByte(encodeShortBuffer, 0, decodedDataLen, tempByteArray, 0);
            fos_decoded_audio.write(tempByteArray, 0, byteLen);

            idx++;
            lastSequenceNumber = sequenceNumber;
        }
        fos_decoded_audio.close();

        System.out.println("Total lost frame: " + count);
    }
}
