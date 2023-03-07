package veinthrough.test.io.Serializable;

import lombok.*;
import veinthrough.api.security.Encrypt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Tests:
 * 1. static/transient member will not automatically be written/read, you can manually handle it in writeObject/readObject.
 * password is a typical example for transient member which is handled manually.
 * 2. class containing Socket/Thread member can't be Serializable.
 * 3. Although a serialVersionUID is auto-generated, it's recommended be maintained manually.
 * It's used to mark different versions of the class.
 * If the class has been changed, you should modify the value of serialVersionUID.
 * 4. Handle the write/read procedure manually in writeObject/readObject.
 * 5. writeUTF   -- readUTF, will write length;
 *    writeChars -- readChar, will not write length, you should know the length before read.
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User implements Serializable {
    // 3. Although a serialVersionUID is auto-generated, it's recommended be maintained manually
    private static final long serialVersionUID = 8598102687435271772L;

    // 1. static/transient member will not automatically be written/read
    @Getter private static String company = "pica8";
    @Getter private static Encrypt encryptor = new Encrypt();
    @NonNull private String name;
    @NonNull private transient String password;
    @NonNull private String addr;
    private String Email;

    // 4. Handle write manually in writeObject
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        // writeUTF <--> readUTF, will write length;
        // writeChars <--> readChar, will not write length, you should know the length before read.
        out.writeUTF(company);
        // out.writeChars(company);
        out.writeUTF(encryptor.xor(password));
    }

    // 4. Handle read manually in readObject
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        company = in.readUTF();
        // Just for test static member
        company = "pica8_from_serialization";
        password = encryptor.xor(in.readUTF());
    }
}
