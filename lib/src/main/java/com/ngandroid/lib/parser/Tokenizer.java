package com.ngandroid.lib.parser;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by davityle on 1/13/15.
 */
public class Tokenizer {

    // supported syntax model.field   function(parameter)

    public static final class Token {
        private final TokenType tokenType;
        private final String script;

        private Token(TokenType tokenType, String script) {
            this.tokenType = tokenType;
            this.script = script;
        }

        public TokenType getTokenType() {
            return tokenType;
        }

        public String getScript() {
            return script;
        }
    }

    public enum TokenType {
        MODEL_NAME,
        MODEL_FIELD,
        FUNCTION_NAME,
        FUNCTION_PARAMETER
    }

    private enum State {
        BEGIN,
        CHAR_SEQUENCE,
        MODEL_FIELD,
        FUNCTION_PARAMETER,
        MODEL_FIELD_END,
        OPEN_PARENTHESIS,
        CLOSE_PARENTHESIS,
        WHITESPACE,
        END,
        COMMA, PERIOD
    }

    private int index, readIndex;
    private String script;
    private Queue<Token> tokens;

    public Tokenizer(String script){
        this.script = script;
    }

    public Queue<Token> getTokens(){
        if(tokens == null){
            generateTokens();
        }
        return tokens;
    }

    private void generateTokens() {
        tokens = new LinkedList<>();
        index = 0;
        readIndex = 0;

        State state = State.BEGIN;
        while (state != State.END) {
            state = nextState(state);
        }
    }

    private State nextState(State state) {
        State result;
        switch (state) {
            case BEGIN:
            case CHAR_SEQUENCE:
                result = getNextState();
                if(peek() == '.'){
                    emit(TokenType.MODEL_NAME);
                }else if(peek() == '('){
                    emit(TokenType.FUNCTION_NAME);
                }
                break;
            case PERIOD:
                result = State.MODEL_FIELD;
                discard();
                break;
            case MODEL_FIELD: {
                State current = getNextState();
                if(!Character.isLetterOrDigit(peek())){
                    emit(TokenType.MODEL_FIELD);
                    result = current;
                }else{
                    result = State.MODEL_FIELD;
                }
                break;
            }
            case FUNCTION_PARAMETER: {
                State current = getNextState();
                if(peek() == ')' || peek() == ','){
                    emit(TokenType.FUNCTION_PARAMETER);
                }
                if (current == State.CHAR_SEQUENCE || current == State.WHITESPACE) {
                    result = State.FUNCTION_PARAMETER;
                } else{
                    result = current;
                }
                break;
            }
            case COMMA:
            case OPEN_PARENTHESIS:
                result = State.FUNCTION_PARAMETER;
                discard();
                break;
            case CLOSE_PARENTHESIS:
                result = getNextState();
                discard();
                break;
            case WHITESPACE:
                result = getNextState();
                break;
            case END:
                result = State.END;
                break;
            default:
                // TODO error
                throw new RuntimeException();
        }
        return result;
    }

    private State getNextState() {
        try {
            if (index == script.length()) {
                return State.END;
            }

            char currentCharacter = script.charAt(index);

            if (Character.isWhitespace(currentCharacter)) {
                return State.WHITESPACE;
            }
            if (Character.isLetterOrDigit(currentCharacter)) {
                return State.CHAR_SEQUENCE;
            }

            switch (currentCharacter) {
                case ',':
                    return State.COMMA;
                case '.':
                    return State.PERIOD;
                case '(':
                    return State.OPEN_PARENTHESIS;
                case ')':
                    return State.CLOSE_PARENTHESIS;
            }
            // TODO throw error
            throw new RuntimeException();
        }finally {
            index++;
        }
    }

    private char peek(){
       return index < script.length() ? script.charAt(index) : 0;
    }

    private void discard(){
        readIndex = index;
    }

    private void emit(TokenType tokenType) {
        Token token = new Token(tokenType, script.substring(readIndex, index).trim());
        discard();
        tokens.add(token);
    }

}