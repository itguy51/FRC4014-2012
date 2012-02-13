/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hightechhighfives.yearone;

import edu.wpi.first.wpilibj.*;

/**
 *
 * @author josh
 */
public class LimitSwitch {

    protected DigitalInput di;

    public LimitSwitch(int portNumber) {
        di = new DigitalInput(portNumber);
    }

    public boolean isOpen(){
        if (di.get() == true) {
            return true;
        } else return false;
    }

    public boolean isClosed(){
        return (!isOpen());
    }

}

