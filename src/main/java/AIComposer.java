import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.utils.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static spark.Spark.*;

public class AIComposer {

    static final String root = "aicomposertmp/";

    public static void main(String[] args) {

        Spark.exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });

        port(8080);

        secure("letsencrypt.jks", "welcome", null, null);

        File theDir = new File(root);

        if (!theDir.exists()) {
            try{
                theDir.mkdir();
            }
            catch(SecurityException se){
                se.printStackTrace();
            }
        }

        File theDir2 = new File(root+"score");

        if (!theDir.exists()) {
            try{
                theDir2.mkdir();
            }
            catch(SecurityException se){
                se.printStackTrace();
            }
        }

        post("/score",((request, response) -> {
            String id = request.queryParams("id");

            //midi2abc gg.midi >> gg.abc && abcm2ps gg.abc -O gg.ps -q && ps2pdf gg.ps gg.pdf
//            Process renderCall = Runtime.getRuntime().exec(new String[] { "./render.sh", root.substring(0,root.length()-1), id});
//            renderCall.waitFor();

            byte[] bytes = Files.readAllBytes(Paths.get(root+"generated/"+id+".pdf"));
            HttpServletResponse raw = response.raw();
            raw.getOutputStream().write(bytes);
            raw.getOutputStream().flush();
            raw.getOutputStream().close();

            return response.raw();
        }));

        post("/mp3",((request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            System.out.println("fuck fuck fuck");
            String id;

            //timidity 5f00c729-6c04-4404-8374-a968313b4154.mid -Ow -o - | lame - -b 64 fff.mp3
//            Process renderCall = Runtime.getRuntime().exec(new String[] { "bash", "-c", "timidity "+root+"generated/"+id+".mid -Ow -o - | lame - -b 64 "+root+"generated/"+id+".mp3"});
//            renderCall.waitFor();

            try (InputStream is = request.raw().getPart("id").getInputStream()) {
                String result = IOUtils.toString(is);
                System.out.println("id: " + result);
                id=result;
            }catch (Exception e){
                return "ERROR: \n\n\n"+e.toString();
            }



            System.out.println(System.nanoTime() + " download request " + id);

            byte[] bytes = Files.readAllBytes(Paths.get(root+"generated/"+id+".mp3"));
            HttpServletResponse raw = response.raw();
            raw.getOutputStream().write(bytes);
            raw.getOutputStream().flush();
            raw.getOutputStream().close();

            return response.raw();
        }));

        enableCORS("*", "*", "*");


//        options("/*",
//                (request, response) -> {
//
//                    String accessControlRequestHeaders = request
//                            .headers("Access-Control-Request-Headers");
//                    if (accessControlRequestHeaders != null) {
//                        response.header("Access-Control-Allow-Headers",
//                                accessControlRequestHeaders);
//                    }
//
//                    String accessControlRequestMethod = request
//                            .headers("Access-Control-Request-Method");
//                    if (accessControlRequestMethod != null) {
//                        response.header("Access-Control-Allow-Methods",
//                                accessControlRequestMethod);
//                    }
//
//                    return "OK";
//                });
//
//        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        post("/compute", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
//            String fileType = request.queryParams("type");
//            String task = request.queryParams("task");

            String id = UUID.randomUUID().toString();

//            System.out.println(System.nanoTime() + "[!#!] " + task + " task received with file type " + fileType);

            String fileType, task;

            try (InputStream is = request.raw().getPart("type").getInputStream()) {
                String result = IOUtils.toString(is);
                System.out.println("type: " + result);
                fileType=result;
            }catch (Exception e){
                return "ERROR: \n\n\n"+e.toString();
            }

            try (InputStream is = request.raw().getPart("task").getInputStream()) {
                String result = IOUtils.toString(is);
                System.out.println("task: " + result);
                task=result;
            }catch (Exception e){
                return "ERROR: \n\n\n"+e.toString();
            }

            File upload = new File(root + id + "." + fileType);
            if (!fileType.equalsIgnoreCase("auto")){
                try (InputStream is = request.raw().getPart("source").getInputStream()) {
                    OutputStream outputStream = new FileOutputStream(upload);
                    IOUtils.copy(is, outputStream);
                    outputStream.close();
                } catch (Exception e) {
                    return "ERROR: \n\n\n" + e.toString();
                }

                File midi = SoundToMIDI.convert(id,upload);

                if (task.equalsIgnoreCase("ml")){
//                Process magentaCall = Runtime.getRuntime().exec("bazel run //magenta/models/performance_rnn:performance_rnn_generate -- \\\n" +
//                        "--config=performance_with_dynamics \\\n" +
//                        "--bundle_file=/home/mark/magenta/models/performanceD.mag \\\n" +
//                        "--output_dir=/home/mark/magenta/"+root+"generated \\\n" +
//                        "--output_name="+id+" \\\n" +
//                        "--num_outputs=1 \\\n" +
//                        "--num_steps=3000 \\\n" +
//                        "--primer_midi=/home/mark/magenta/"+root+midi.getName());

                    Process call = Runtime.getRuntime().exec("bazel run //magenta/models/pianoroll_rnn_nade:pianoroll_rnn_nade_generate -- \\\n" +
                            "--bundle_file=/home/mark/magenta/models/pianoroll_rnn_nade.mag \\\n" +
                            "--output_dir=/home/mark/magenta/"+root+"generated \\\n" +
                            "--num_outputs=1 \\\n" +
                            "--num_steps=300 \\\n" +
                            "--condition_on_primer=true \\\n" +
                            "--output_name="+id+" \\\n" +
                            "--inject_primer_during_generation=false \\\n" +
                            "--primer_midi=/home/mark/magenta/"+root+midi.getName());
                    call.waitFor();
                }else{//task == "fugue"
                    Process call = Runtime.getRuntime().exec("bazel run //magenta/models/polyphony_rnn:polyphony_rnn_generate -- \\\n" +
                            "--bundle_file=/home/mark/magenta/models/polyphony_rnn.mag \\\n" +
                            "--output_dir=/home/mark/magenta/"+root+"generated \\\n" +
                            "--num_outputs=1 \\\n" +
                            "--num_steps=400 \\\n" +
                            "--output_name="+id+" \\\n" +
                            "--condition_on_primer=true \\\n" +
                            "--inject_primer_during_generation=false \\\n" +
                            "--primer_midi=/home/mark/magenta/"+root+midi.getName());
                    call.waitFor();
                }
            }else{
                if (task.equalsIgnoreCase("ml")){
                    Process call = Runtime.getRuntime().exec("bazel run //magenta/models/pianoroll_rnn_nade:pianoroll_rnn_nade_generate -- \\\n" +
                            "--bundle_file=/home/mark/magenta/models/pianoroll_rnn_nade.mag \\\n" +
                            "--output_dir=/home/mark/magenta/"+root+"generated \\\n" +
                            "--num_outputs=1 \\\n" +
                            "--num_steps=300 \\\n" +
                            "--output_name="+id+" \\\n" +
                            "--condition_on_primer=true \\\n" +
                            "--inject_primer_during_generation=false");
                    call.waitFor();
                }else{//task == "fugue"
                    Process call = Runtime.getRuntime().exec("bazel run //magenta/models/polyphony_rnn:polyphony_rnn_generate -- \\\n" +
                            "--bundle_file=/home/mark/magenta/models/polyphony_rnn.mag \\\n" +
                            "--output_dir=/home/mark/magenta/"+root+"generated \\\n" +
                            "--num_outputs=1 \\\n" +
                            "--num_steps=400 \\\n" +
                            "--output_name="+id+" \\\n" +
                            "--condition_on_primer=true \\\n" +
                            "--inject_primer_during_generation=false");
                    call.waitFor();
                }
            }

            Process renderCall = Runtime.getRuntime().exec(new String[] { "bash", "-c", "timidity "+root+"generated/"+id+".mid -Ow -o - | lame - -b 64 "+root+"generated/"+id+".mp3"});
            renderCall.waitFor();

            Process renderCall2 = Runtime.getRuntime().exec(new String[] { "./render.sh", root.substring(0,root.length()-1), id});
            renderCall2.waitFor();

            return id;
        });
    }

    private static void enableCORS(final String origin, final String methods, final String headers) {
        before(new Filter() {
            @Override
            public void handle(Request request, Response response) {
                response.header("Access-Control-Allow-Origin", origin);
                response.header("Access-Control-Request-Method", methods);
                response.header("Access-Control-Allow-Headers", headers);
            }
        });
    }

}
