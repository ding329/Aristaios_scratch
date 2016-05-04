# Aristaios_scratch

## Installation Instructions

1. Download the source code from github.  
  * git clone https://github.com/ding329/Aristaios_scratch
2. Attach Android device (tablet or smart phone) to the computer via USB / mini USB cable.
3. Open Android studio
  1. Download and Install Android Studio
    * http://developer.android.com/sdk/index.html
    * http://developer.android.com/sdk/installing/index.html
  2. Open Android studio by opening a command shell and typing “studio.sh” and hit enter.
4. Hardcode the MAC address of your sensor.
  1. Open the MainActivity.java file
    * Aristaios_scratch/app/src/main/java/com/example/student/aristaios/MainActivity.java
  2. Change the value of MW_MAC_ADDRESS to the MAC address of the desired metawear env sensor.
  3. If the MAC address is unknown, the MAC address can be found by downloading “MetaWear” app and scanning for devices.
5. Open the Aristaios_scratch program
  1. In Android Studio select File -> Open
  2. In popup menu select the Aristaios_scratch application and click Okay.
6. Compile the application
  1. Click on the Run button on the toolbar.
  2. A popup menu listing your android device should appear.  Click Okay.
7. The application is now installed onto Android device.

======
## Trouble shooting
### Failure to connect to sensor
  * Validate the device's blue tooth is enabled and that the device can detect the sensor.
  * Attempt to reconnect to the device.
    * there is a known occasional blue tooth error when attempting to connect.  If at first you dont succeed try try again.
