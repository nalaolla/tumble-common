package kr.co.tumble.common.util.masking;

/**
 * AbstractMasker Class
 */
public class AbstractMasker implements Masker {

    @Override
    public String mask(String value) {
        return value;
    }

}