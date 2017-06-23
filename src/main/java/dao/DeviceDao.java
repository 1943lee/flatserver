package dao;

import model.Device;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by hejiangbo on 2016/11/7.
 */
@Repository
public interface DeviceDao {
        //更新设备信息
        public int updateDevice(Map<String,String>device);
        //更新设备信息
        public int insertDevice(Map<String,String>device);
        public int insertDevices(Device device);
        //更新设备位置
        public int updateDevicePostion(Map device);
        //更新设备用户状态
        public int updateUserStatus(Map device);
        //删除设备
        public int deleteDevice(String sbbh);
        public int deleteDevices(Map<String,String>device);
        //重置用户状态
        public int clearUserStatus();
        //获取用户名信息
        public Map getUserName(String userCode);
        //通过设备编号获取设备信息
        public Device getUnitBySbbh(Device device);
        public List<Map<String, String>> getDevice();
        //通过userCode获取用户名信息和设备使用者警种
        public Map getUserNameAndSbsyzlb(String userCode);

}
