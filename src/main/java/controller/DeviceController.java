package controller;

import common.DeviceDbOperate;
import common.JsonUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by hejiangbo on 2016/12/6.
 */
@RestController
@RequestMapping("/{b:(?i)device}")
public class DeviceController {
    /**
     * 更新设备信息rest接口
     * @param
     * @return
     */
    @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
    public String update(@RequestBody String json){
        Map<String, Map> devices = JsonUtils.json2MapDic(json, "sbbh");
        DeviceDbOperate.batchUpdateInfo(devices);
        return "更新成功!";
    }

    /**
     * 删除设备rest接口
     * @param json
     * @return
     */
    @RequestMapping(value = "/deleteInfo", method = RequestMethod.DELETE)
    public String delete(@RequestBody String json){
         Map<String, Map> devices = JsonUtils.json2MapDic(json, "sbbh");
         DeviceDbOperate.batchDeleteInfo(devices);
         return "删除成功!";
    }

}
