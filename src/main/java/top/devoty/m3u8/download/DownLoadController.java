package top.devoty.m3u8.download;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
public class DownLoadController {


    private final RestTemplate restTemplate;

    private final String baseUri = "";

    private final String baseDir = "/Users/devoty/test/baseDir";

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);



    public DownLoadController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @PostMapping ("DownLoad")
    public void doGet(String uri, String fileName){
//        String uri = "https://dtliving-sz.dingtalk.com/live_hp/2e0fd73f-5dce-4767-be9d-ba853d2c17ef_merge.m3u8?app_type=mac&auth_key=1631196563-0-0-7d1feb3c3113a72b09df0a60ba754ab7&cid=4204558287363286f3d1c6a6cfd1df65&token=65a7cdbea4451ba1bdd674064cac46da2enbhxtmKRQK5BUUXFkGf2vfOUkof81INm8xcJt2tdm21olFn2h69f_4Vgg-AApYtMTZccGBKhl0RuoSuB4FRgpxpgAH2xV2_ESsyXM_aPA=&token2=4eb73423406d0ab1a2780b96ce3f2834SVNXdWu3rnK-5U77YlDTQLjyQFxcanNEDZutjLZxwoJ2KA8VmUfJcD7wlkg8_UtNTM7E-LgLk6LhsAKVw9oPVyrATbS0q3W-PBGRh7bVuEc&version=6.0.16";

        String tmpDir = uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf(".m3u8"));

        String toPath = baseUri + tmpDir;

        List<String> ts = getM3U8Source(uri);

        try {
            downloadTs(ts,toPath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        String downLoadFile = fileName.isEmpty() ? tmpDir : fileName;

        try {
            MergeFileUtil.merge(downLoadFile,toPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public List<String> filterTS(List<String> ts){
        return ts.stream().filter(
                tsStr -> {
                    if(tsStr.contains("ts"))
                        return true;
                    return false;
                }
        ).collect(Collectors.toList());
    }

    public List<String> getM3U8Source(String uri){
        String list =  restTemplate.getForObject(URI.create(uri), String.class);
        String[] l = list.split("\n");
        return filterTS(Arrays.asList(l));
    }

    public void downloadTs(List<String> tsList, String toPath) throws IOException, InterruptedException {
        Files.createFile(Paths.get(toPath));

        int iFile = 0;
        for (String ts : tsList) {
            int finalIFile = iFile++;
            Thread downloadThread = new Thread(() -> {
                String uri = baseUri + ts;
                byte[] bytes = restTemplate.getForObject(URI.create(uri), byte[].class);
                try {
                    Path path = Paths.get(toPath, finalIFile + ".ts");
                    Files.write(path, bytes, StandardOpenOption.CREATE);
                    System.out.println(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            executorService.submit(downloadThread);
        }

        do{
            Thread.sleep(1000);
        }while (iFile == tsList.size());

        System.out.println("下载完成");
    }



}
