package com.chinalife.common.exception;

import com.chinalife.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ClException extends RuntimeException {

    private ExceptionEnum exceptionEnum;

}
