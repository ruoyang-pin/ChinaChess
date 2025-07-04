package com.util;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

/**
 * @author rich
 * @date 2025/6/21
 * @description
 */
public class SoundPlayer {

    public static void playClickSound(String name) {
        try {
            URL soundURL = SoundPlayer.class.getClassLoader().getResource("music/" + name);
            if (soundURL == null) {
                System.err.println("音频文件未找到！");
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.err.println("播放音频失败: " + e.getMessage());
        }
    }

}
