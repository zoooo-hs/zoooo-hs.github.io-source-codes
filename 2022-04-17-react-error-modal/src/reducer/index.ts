import {combineReducers, createStore} from "redux";
import {errorState} from "./error-state";

const rootReducer = combineReducers({
    errorState
})

export default rootReducer;

export type RootState = ReturnType<typeof rootReducer>
export const store = createStore(rootReducer);
