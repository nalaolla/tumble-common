package kr.co.tumble.common.util;



import kr.co.tumble.common.constant.TumbleConstants;
import kr.co.tumble.common.entity.Swearword;
import kr.co.tumble.common.service.SwearwordRestService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

/**
 * SwearwordUtils Class
 */
@Component
@RequiredArgsConstructor
public class SwearwordUtils {

    private final Swearword.SwearwordRequest swearwordRequest = Swearword.SwearwordRequest.builder().useYn(TumbleConstants.Y).langCd(TumbleConstants.DEFAULT_LANG_CD_VALUE).build();
    private static final String DEFAULT_REPLACEMENT = "♡";

    private final SwearwordRestService swearwordRestService;

    public Collection<String> getSwearwords() {
        return swearwordRestService.getSwearwords(swearwordRequest);
    }

    public void swearwordCacheEvict() {
        swearwordRestService.swearwordCacheEvict(swearwordRequest);
    }

    public Swearword.SwearwordReplaceModel replaceWith(final String str) {
        return replaceWith(str, DEFAULT_REPLACEMENT);
    }

    public Swearword.SwearwordReplaceModel replaceWith(final String str, final String replacement) {
        Collection<String> swearwords = this.getSwearwords();
        return new Swearword.SwearwordReplaceModel(str, replacement, swearwords);
    }

    public boolean isSwearword(final String word) {
        Collection<String> swearwords = this.getSwearwords();
        Optional<String> result =
                swearwords.parallelStream()
                        .filter(w -> StringUtils.equalsIgnoreCase(word, w))
                        .findFirst();

        return result.isPresent();
    }

    public boolean isSwearwordIncluded(final String word) {
        Collection<String> swearwords = this.getSwearwords();
        Optional<String> result =
                swearwords.parallelStream()
                        .filter(w -> StringUtils.indexOfAny(word, w) > -1)
                        .findFirst();

        return result.isPresent();
    }
}