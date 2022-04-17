import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../reducer";
import { hideError } from "../reducer/error-state";
import "./error-modal.css";

export function ErrorModal() {
    const {error, visible} = useSelector((state: RootState) => state.errorState);
    const dispatch = useDispatch();

    if (!visible || error === undefined) {
        return null;
    } 

    function handleClose() {
        dispatch(hideError());
    }

    const {code, message} = error;

    return (
        <div className="error-modal">
            <h1>{`[오류 코드 : ${code}] 오류 발생`}</h1>
            <p>{message}</p>
            <button onClick={handleClose}>닫기</button>
        </div>
    )
}