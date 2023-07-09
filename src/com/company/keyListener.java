package com.company;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class keyListener implements KeyListener {
    public boolean[] directionalKeys = new boolean[6];
    public boolean[] rotationalKeys = new boolean[6];
    private final HashMap<Character, axisKeys>  keyToDirAxis = new HashMap<>(
            Map.of('a', axisKeys.POS_X, 'd', axisKeys.NEG_X,
                    'e', axisKeys.POS_Y, 'q', axisKeys.NEG_Y,
                    's', axisKeys.POS_Z, 'w', axisKeys.NEG_Z));
    private final HashMap<Character, axisKeys>  keyToRotAxis = new HashMap<>(
            Map.of('i', axisKeys.POS_X, 'k', axisKeys.NEG_X,
                    'j', axisKeys.POS_Y, 'l', axisKeys.NEG_Y,
                    'u', axisKeys.POS_Z, 'o', axisKeys.NEG_Z));
  //  Map.of('j', axisKeys.POS_X, 'l', axisKeys.NEG_X,
    //        'o', axisKeys.POS_Y, 'u', axisKeys.NEG_Y,
      //      'k', axisKeys.POS_Z, 'i', axisKeys.NEG_Z));

           @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        HandleKeyEvent(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        HandleKeyEvent(e, false);
    }

    private void HandleKeyEvent(KeyEvent e, boolean on) {
        char c = e.getKeyChar();
        if (keyToDirAxis.containsKey(c)){
            directionalKeys[keyToDirAxis.get(c).ordinal()] = on;
        }
        else if (keyToRotAxis.containsKey(c)){
            rotationalKeys[keyToRotAxis.get(c).ordinal()] = on;
        }
    }
}
