import { useDispatch } from "react-redux";
import { ErrorModal } from "./component/error-modal";
import { ErrorModel } from "./model/error-model";
import { showError } from "./reducer/error-state";

function troubleMaker(): Promise<ErrorModel> {
  return Promise.reject({
    code: "TROUBLE_MAKER",
    message: `새로운 오류! 현재 시간은 ${new Date()}`
  })
}

function App() {

  const dispatch = useDispatch();

  function handleClick() {
    troubleMaker().then(() => {
      // never
    }).catch(error => {
      dispatch(showError(error));
    });
  }

  return (
    <div>
      <ErrorModal/>
      <button onClick={handleClick}>트러블 메이커</button>
    </div>
  );
}

export default App;
