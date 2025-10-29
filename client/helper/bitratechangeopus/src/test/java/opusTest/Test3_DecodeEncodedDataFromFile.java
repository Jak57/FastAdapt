package opusTest;

import com.reve.audio.libraries.dlls.codec.opus.OPUSDecoder;
import com.reve.util.ShortArrayUtil;
import org.junit.jupiter.api.Test;

import java.io.*;

public class Test3_DecodeEncodedDataFromFile {

    @Test
    void applyFilters() throws IOException {
        int sampleRate = 48000;
        int bitRate = 48000;
        int processingTimeMs = 10 * 2;
        int channel = 1;
        int chunkSizeShort = (sampleRate * processingTimeMs * channel) / 1000;

        String folder = "F:\\projects\\office_main_projects\\BitrateControlServer\\out\\artifacts\\BitrateControlServer_jar\\";

//        String folder = "F:\\audio\\bitrate_control_loss_based_test\\send_packet\\input\\file_writing\\";
        String binaryFilePath = folder + "decoded_binary.bin";
        String csvFilePath = folder + "decoded_csv.csv";
        String decodedAudioFilePath = "F:\\audio\\bitrate_control_loss_based_test\\send_packet\\input\\file_writing\\jakir_48khz_decoded_audio_dynamic2.raw";

        FileInputStream fis_binary = new FileInputStream(binaryFilePath);
        FileOutputStream fos_decoded_audio = new FileOutputStream(decodedAudioFilePath);
        byte[] encodedData = fis_binary.readAllBytes();
        System.out.println(encodedData.length);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFilePath));
        String len;
        short[] encodeShortBuffer = new short[chunkSizeShort];
        byte[] tempByteArray = new byte[chunkSizeShort * 2];
        OPUSDecoder opusDecoder = new OPUSDecoder(sampleRate, bitRate, processingTimeMs);

        int index = 0;
        while ((len = bufferedReader.readLine()) != null) {
            int encodeBufferLen = Integer.valueOf(len);
            byte[] encodeDataByteCopy = new byte[encodeBufferLen];
            System.arraycopy(encodedData, index, encodeDataByteCopy, 0, encodeBufferLen);
            index += encodeBufferLen;

            int decodedDataLen = opusDecoder.decode(encodeDataByteCopy, 0, encodeBufferLen, encodeShortBuffer, 0);
            int byteLen = ShortArrayUtil.convertShortToByte(encodeShortBuffer, 0, decodedDataLen, tempByteArray, 0);
            fos_decoded_audio.write(tempByteArray, 0, byteLen);
        }
        fos_decoded_audio.close();
    }
}
