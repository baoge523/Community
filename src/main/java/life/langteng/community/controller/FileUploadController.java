package life.langteng.community.controller;

import com.cyou.common.base.log.CyouLogger;
import com.cyou.common.base.log.annotation.LogPoint;
import life.langteng.community.annotation.NeedLogin;
import life.langteng.community.dto.FileUploadDTO;
import life.langteng.community.service.IFileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


/**
 * 所有的方法都以json的格式传递出去
 */
@NeedLogin
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private IFileUploadService fileUploadService;

    /**
     * markdown 上传图片，指定了 MultipartFile 的名称  editormd-image-file
     * @param multipartFile
     * @return
     */
    @RequestMapping("/upload")
    @LogPoint(type = CyouLogger.Type.ACCESS,message = "上传图片")
    public FileUploadDTO fileUpload(
            @RequestParam("editormd-image-file") MultipartFile multipartFile){



        FileUploadDTO fileUploadDTO = fileUploadService.fileUpload(multipartFile);

        return fileUploadDTO;
    }



}
