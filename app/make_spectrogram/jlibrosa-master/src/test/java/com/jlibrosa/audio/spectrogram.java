package com.jlibrosa.audio;

import java.lang.Math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileWriter;

import org.apache.commons.math3.complex.Complex;

import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.process.AudioFeatureExtraction;
import com.jlibrosa.audio.wavFile.WavFile;
import com.jlibrosa.audio.wavFile.WavFileException;



"""
@para String fileroot 파일이 저장된 위치
@para String filename 파일 위치
@para int timeunit 자르는 시간 (디폴트 128)

@return mel - spectrogram 4차원 array 타입
"""

public class spectrogram {
    public static ArrayList<ArrayList<ArrayList<ArrayList<float>>>> make_spectrogram(String fileroot, String filename, int timeUnit) {

        // parameters
        private int sample_rate = 16000;
        private int n_fft = sample_rate * 0.025;
        private int hop_length = sample_rate * 0.01;
        private int n_mel = 128;

        // Jlibrosa
        JLibrosa jLibrosa = new JLibrosa();

        // wav 읽기
        float audioFeatureValues[] = jLibrosa.loadAndReadWithOffset(fileroot + filename, sample_rate, -1, 0);

        // 정규화
        float ymin, float ymax = Math.max(audioFeatureValues), Math.min(audioFeatureValues);
        for (int i = 0; i < audioFeatureValues.length(); i++) {
            audioFeatureValues[i] = (audioFeatureValues[i]) * 2 / (ymax - ymin) - 1;
        }

        // 스펙트로 그램 저장
        float[][] melSpectrogram = jLibrosa.generateMelSpectroGram(audioFeatureValues, sample_rate, n_fft, n_mel, hop_length);

        // power_to_db => ( 10 * log10( S / ref) ref = abs(S)
        melSpectrogram = jLibrosa.powerToDb(melSpectrogram);

        // 프레임수 frame -> 행, key -> 렬
        int n_frame = melSpectrogram[1].length;
        int n_key = melSpectrogram[:,1].length;

        // 차분과 차차분 구하고, stack 맞추기 (일단 0으로 맞춤)
        float[] delta1 = new float[n_frame];
        float[] delta2 = new float[n_frame];


        """delta1 = librosa.feature.delta(data=mel, width=5)
            delta2 = librosa.feature.delta(data=delta1, width=5)
            stack = np.dstack((mel,delta1,delta2))"""

        // 4차원 array list 생성해서 각각 담기
        ArrayList<ArrayList<ArrayList<ArrayList<float>>>> mel_list = new ArrayList<ArrayList<ArrayList<ArrayList<float>>>>;

        for (int i = 0; i < n_frame - timeUnit + 1; i += timeUnit) {
            ArrayList<ArrayList<ArrayList<float>>> three_list = new ArrayList<ArrayList<ArrayList<float>>>;
            for (int j = 0; j < n_key; j++) {
                ArrayList<ArrayList<float>> two_list = new ArrayList<ArrayList<float>>;
                for (k = 0; k < n_frame; k++) {
                    ArrayList<float> one_list = {melSpectrogram[j][k], delta1[k], delta2[k]};
                    two_list.add(one_list);
                }
                three_list.add(two_list);
            }
            mel_list.add(three_list[:,i:i + timeUnit]);
        }

        return mel_list;
    }

    public stactic void main(String[] args)  {
        ArrayList hello = make_spectrogram("C:\\Users\\dbghk\\Desktop\\runner\\moodots\\moodots\\app\\make_spectrogram\\jlibrosa-master\\audioFiles", '001_children_playing.wav', 128);
        System.out.println(hello);
    }
}