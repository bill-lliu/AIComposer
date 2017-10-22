import com.mashape.unirest.http.Unirest;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class SoundToMIDI {

    static File convert(String id, File upload) throws Exception{
        JSONArray notes = Unirest.post("http://api.sonicapi.com/analyze/melody")
                .queryString("access_id", "b5684527-b001-4b5a-adfd-d1c206151c21")
                .queryString("format", "json")
                .field("input_file",upload)
                .asJson().getBody().getObject().getJSONObject("melody_result").getJSONArray("notes");

        Pattern pattern = new Pattern();
        int prePitch = 0;
        boolean f = true;
        for (int i = 0; i < notes.length(); i++) {
            JSONObject note = notes.getJSONObject(i);
            int pitch = (int) Math.round(note.getDouble("midi_pitch"));
            double duration = note.getDouble("duration");
            double volume = note.getDouble("volume");
            if (duration>0.1&&volume>0.008&&(Math.abs(prePitch-pitch)<15||f)) {
                pattern.add(new Note(pitch, duration / 2));
                prePitch = pitch;
                f=false;
            }
        }

        File midi =  new File(AIComposer.root+"TMP"+id+".midi");
        MidiFileManager.savePatternToMidi(pattern, midi);
        return midi;
    }

}
