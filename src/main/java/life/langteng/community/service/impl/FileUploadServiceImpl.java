package life.langteng.community.service.impl;

import com.cyou.common.base.log.CyouLogger;
import com.cyou.common.base.log.annotation.LogPoint;
import life.langteng.community.dto.FileUploadDTO;
import life.langteng.community.service.IFileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@LogPoint(type = CyouLogger.Type.INVOKE,message = "调用上传图片服务")
public class FileUploadServiceImpl implements IFileUploadService {

    private final Logger logger = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    @Override
    public FileUploadDTO fileUpload(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            logger.info("image upload fail");
            return FileUploadDTO.fail();
        }

        String uuid = UUID.randomUUID().toString().substring(0, 10).replaceAll("-","a");

        String fileName = uuid + "-" + file.getOriginalFilename();

        // 从环境变量中获取当前项目的路径
        String rootPath = System.getProperty("user.dir");

        String filePath =rootPath+"\\src\\main\\resources\\static\\images\\"+ fileName;

        logger.info("当前存放图片的绝对路径"+filePath);

        File f = new File(filePath);

        try {
            file.transferTo(f);
        } catch (IOException e) {
            logger.info("图片上传失败",e);
            return FileUploadDTO.fail();
        }
        // 这里必须是相对了classpath路径，不然会报错
        return FileUploadDTO.success("/images/"+fileName);
    }
}
