package kr.co.tumble.common.entity;


import kr.co.tumble.common.exception.CommonException;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * AbstractEntity 객체
 */
public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = -3144023307496112743L;

    public String toBase64(){
        try(
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		ObjectOutputStream oos = new ObjectOutputStream(baos);
        ) {
        	oos.writeObject(this);
        	oos.flush();
        	return String.valueOf(Base64.encodeBase64String(baos.toByteArray())).replaceAll("[\n\r]", "");
        } catch (IOException ioe) {
            throw new CommonException(ioe);
        }
    }

}
