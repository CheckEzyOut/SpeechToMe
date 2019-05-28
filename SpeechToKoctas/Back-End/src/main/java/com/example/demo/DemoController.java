package com.example.demo;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;

import org.apache.log4j.Logger;

@RestController
public class DemoController {

	final static Logger logger = Logger.getLogger(DemoController.class);
	
	@RequestMapping(value="/uploadVoice",method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<Object> uploadVoice(@RequestParam("data") String multipartFile) throws IOException {
		String text = "";
		String transactionId = String.valueOf(multipartFile.length() % 999);
		logger.info(transactionId + ": UploadVoice begin");
	    try {
	    	text = execute(multipartFile, 44100);
	    	logger.info(transactionId + ": 44100 is ok");
		} catch(Exception ex) {
			try {
				logger.error(transactionId + ": Default hertz incompatible. Calculating hertz again: " + ex.getMessage());
				int hertz = handleHertzErrorParser(ex.getMessage());
				text = execute(multipartFile, hertz);
				logger.info(transactionId + ": "+String.valueOf(hertz)+" is ok");
			} catch (Exception e) {
				logger.error(transactionId + ": Calculated hertz incompatible. Returning String.EMPTY: " + ex.getMessage());
				text = "";
			}
		}
	    logger.info(transactionId + ": Succeeded with[" + text + "]");
	    return new ResponseEntity<Object>(new ResponseText(text), HttpStatus.OK);
    }
	
	@RequestMapping(value="/",method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<Object> hellotest1() throws IOException {
		return new ResponseEntity<Object>(new ResponseText("hello world."), HttpStatus.OK);
    }
	
	@RequestMapping(value="/hello",method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<Object> hellotest2() throws IOException {
		return new ResponseEntity<Object>(new ResponseText("hello world.."), HttpStatus.OK);
    }
	
	private String execute(String multipartFile, int hertz) throws IOException, Exception {
		String text = "";
		byte[] data = org.apache.commons.codec.binary.Base64.decodeBase64(multipartFile.getBytes());
    	SpeechClient speechClient = SpeechClient.create();
		ByteString audioBytes = ByteString.copyFrom(data);
		RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16).setSampleRateHertz(hertz).setLanguageCode("tr-TR").build();
		RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();
		RecognizeResponse response = speechClient.recognize(config, audio);
		List<SpeechRecognitionResult> results = response.getResultsList();

		for (SpeechRecognitionResult result : results) {
			SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
			text += alternative.getTranscript();
		}
		return text;
	}
	
	private int handleHertzErrorParser(String hertz) {
		int handledHertz = 48000;
		try {
			handledHertz = Integer.parseInt(hertz.substring(hertz.length() - 8).replace("(", "").replaceAll(")", "").replaceAll(".", "").trim());
		} catch (Exception ex) {
			handledHertz = 48000;
		}
		return handledHertz;
	}
}

