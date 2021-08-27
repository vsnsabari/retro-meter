import { customAlphabet } from "nanoid";
import React from "react";
import { useContext } from "react";
import { createContext } from "react";
import { useReducer } from "react";
import { SessionModel } from "../../models/SessionModel";

const nanoid = customAlphabet('1234567980abcdefghijklmnopqrstuvwxyz', 10);
const currentClientId = nanoid();

export type Action = {
    type: "CLIENT_ID" | "NAME" | "SESSION";
    data: typeof defaultContext;
}
export type Dispatch = (action: Action) => void;
export type State = typeof defaultContext;

export const CurrentContext = createContext<{ currentState: State, dispatch: Dispatch } | undefined>(undefined);

const defaultContext = {
    clientId: currentClientId,
    name: "ananymous",
    session: new SessionModel()
}

function contextReducer(state: State, action: Action) {
    switch (action.type) {
        case 'CLIENT_ID':
            return {
                clientId: action.data.clientId,
                name: state.name,
                session: state.session
            }
        case 'NAME':
            return {
                clientId: state.clientId,
                name: action.data.name,
                session: state.session
            }
        case 'SESSION':
            return {
                clientId: state.clientId,
                name: state.name,
                session: action.data.session
            }
    }
}

export const ContextProvider: React.FC = ({ children }) => {
    const [state, dispatch] = useReducer(contextReducer, defaultContext);
    return (
        <CurrentContext.Provider value={{ currentState: state, dispatch }}>
            {children}
        </CurrentContext.Provider >
    )
}

export const useCurrentContext = () => {
    const context = useContext(CurrentContext);
    if (!context) {
        throw new Error("useCurrentContext must be inside a ContextProvider");
    }
    return context;
}