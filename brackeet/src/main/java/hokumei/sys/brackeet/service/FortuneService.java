package hokumei.sys.brackeet.service;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.FortuneResult;

public interface FortuneService {
    Result<FortuneResult> getFortune(String birthday);
}
