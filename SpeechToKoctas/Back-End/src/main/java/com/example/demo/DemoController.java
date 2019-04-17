package com.example.demo;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;

@RestController
public class DemoController {

	@RequestMapping(value="/uploadVoice",method = RequestMethod.POST)
    public @ResponseBody String uploadVoice(@RequestParam("file") MultipartFile multipartFile) throws IOException
    {
		byte[] data = multipartFile.getBytes();
    	System.out.println(data.length+"");
	    try (SpeechClient speechClient = SpeechClient.create()) {
			ByteString audioBytes = ByteString.copyFrom(data);
			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
					.setSampleRateHertz(16000).setLanguageCode("tr-TR").build();
			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();
			RecognizeResponse response = speechClient.recognize(config, audio);
			List<SpeechRecognitionResult> results = response.getResultsList();

			for (SpeechRecognitionResult result : results) {
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				System.out.printf("Transcription: %s%n", alternative.getTranscript());
			}
		}
	    return "file uploaded";

    }

}
