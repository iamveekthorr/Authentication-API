/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Victor Okonkwo
 * @param <T>
 */
public interface TryCatch<T> {

    void acceptParams(HttpServletRequest req, HttpServletResponse res);

    default TryCatch<T> andThen(TryCatch<? super T> after) {
        Objects.requireNonNull(after);
        return (HttpServletRequest rs, HttpServletResponse rq) -> { acceptParams(rs, rq); after.acceptParams(rs, rq);};
    }
}
