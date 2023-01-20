package starter.devices;

import lombok.Value;

import java.util.Date;

@Value
public class Device
{
    String deviceName;
    String osType;
    String countryCode;
    Date createdDate;
}
