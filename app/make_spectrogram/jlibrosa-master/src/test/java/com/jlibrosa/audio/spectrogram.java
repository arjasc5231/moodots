package com.jlibrosa.audio;

import java.lang.Math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;

import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFileException;

import jpublic class spectrogram {
    public static void main(String[] args) {
        public float [][] make_spectrogram(String fileroot, String filename, int timeUnit){

            // parameters
            private int sample_rate = 16000;
            private int n_fft = int(sample_rate * 0.025);
            private int hop_length = int(sample_rate * 0.01);
            private int n_mel = int(128);
            ArrayList mel_list = new ArrayList();

            // Jlibrosa
            JLibrosa jLibrosa = new JLibrosa();

            // wav 읽기
            float audioFeatureValues[] = jLibrosa.loadAndReadWithOffset(fileroot + filename, sample_rate, -1, 0);

            // 정규화
            float ymin, float ymax =  Math.max(audioFeatureValues), Math.min(audioFeatureValues);
            for(int i=0; i < audioFeatureValues.length(); i++) {
                audioFeatureValues[i] = (audioFeatureValues[i]) * 2/ (ymax-ymin) -1 ;
            }

            // 스펙트로 그램 저장
            float[][] melSpectrogram = jLibrosa.generateMelSpectroGram(audioFeatureValues, sample_rate, n_fft, n_mel, hop_length);

            // power_to_db ( 10 * log10( S / ref) ref = abs(S)

            // 시간 별로 세기 값들에 접근해서, 절대값으로 나눠주고 바꾼 값 다시 넣음음
            for(int i=0; i < melSpectrogram[i][0].length(); i++){
                for(int j=0; j < melSpectrogramp[i][j].length(); j++) {
                    melSpectrogram[i][j] = 10 * Math.log10(melSpectrogram[i][j] / Math.abs(melSpectrogram[i, :]))
                }
            }

            // 차분과 차차분 구하고, stack 맞추기

            """delta1 = librosa.feature.delta(data=mel, width=5)
            delta2 = librosa.feature.delta(data=delta1, width=5)
            stack = np.dstack((mel,delta1,delta2))"""


            // timetimeUnit에 맞춰서 분배하기

            for(int i=0; i < audioFeatureValues.length() - timeUnit + 1; i += timeUnit;) {
                mel_list.add(audioFeatureValues[:, i:i+timeUnit])
            }

            return mel_list;
        }
    }
    float [][] hello = make_spectrogram("C:\Users\dbghk\Desktop\runner\moodots\moodots\app\make_spectrogram\jlibrosa-master\audioFiles\", "001_children_playing.wav", 128)
}