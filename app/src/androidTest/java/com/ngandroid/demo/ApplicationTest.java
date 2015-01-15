package com.ngandroid.demo;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.ngandroid.lib.parser.SyntaxParser;
import com.ngandroid.lib.parser.Tokenizer;

import java.util.Queue;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    Application testApplication;

    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        testApplication = getApplication();
    }

    public void testTokenizer(){

        Tokenizer tokenizer = new Tokenizer("testName.testField");
        Queue<Tokenizer.Token> tokenqueue = tokenizer.getTokens();

        assertTrue(tokenqueue.size() == 3);
        Tokenizer.Token token = tokenqueue.poll();
        System.out.println(token.getTokenType());
        assertTrue(token.getTokenType() == Tokenizer.TokenType.MODEL_NAME);
        System.out.println(token.getScript());
        assertTrue(token.getScript().equals("testName"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.MODEL_FIELD);
        System.out.println(token.getScript());
        assertTrue(token.getScript().equals("testField"));


        tokenizer = new Tokenizer("functionName(parameter)");
        tokenqueue = tokenizer.getTokens();

        assertTrue(tokenqueue.size() == 3);
        token = tokenqueue.poll();
        System.out.println(token.getTokenType());
        assertTrue(token.getTokenType() == Tokenizer.TokenType.FUNCTION_NAME);
        System.out.println(token.getScript());
        assertTrue(token.getScript().equals("functionName"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.FUNCTION_PARAMETER);
        System.out.println(token.getScript());
        assertTrue(token.getScript().equals("parameter"));

        tokenizer = new Tokenizer("functionName(parameter , parameter2  )");
        tokenqueue = tokenizer.getTokens();

        assertTrue(tokenqueue.size() == 4);
        token = tokenqueue.poll();
        System.out.println(token.getTokenType());
        assertTrue(token.getTokenType() == Tokenizer.TokenType.FUNCTION_NAME);
        System.out.println(token.getScript());
        assertTrue(token.getScript().equals("functionName"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.FUNCTION_PARAMETER);
        System.out.println(token.getScript());
        assertTrue(token.getScript().equals("parameter"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.FUNCTION_PARAMETER);
        System.out.println(token.getScript());
        assertTrue(token.getScript().equals("parameter2"));

        tokenizer = new Tokenizer(" modelName.boolValue ? functionName(parameter , parameter2  ) : modelName.stringValue");
        tokenqueue = tokenizer.getTokens();

        assertTrue(tokenqueue.size() == 10);
        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.MODEL_NAME);
        assertTrue(token.getScript().equals("modelName"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.MODEL_FIELD);
        assertTrue(token.getScript().equals("boolValue"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.TERNARY_QUESTION_MARK);
        assertTrue(token.getScript().equals("?"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.FUNCTION_NAME);
        assertTrue(token.getScript().equals("functionName"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.FUNCTION_PARAMETER);
        assertTrue(token.getScript().equals("parameter"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.FUNCTION_PARAMETER);
        assertTrue(token.getScript().equals("parameter2"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.TERNARY_COLON);
        assertTrue(token.getScript().equals(":"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.MODEL_NAME);
        assertTrue(token.getScript().equals("modelName"));

        token = tokenqueue.poll();
        assertTrue(token.getTokenType() == Tokenizer.TokenType.MODEL_FIELD);
        assertTrue(token.getScript().equals("stringValue"));


        tokenizer = new Tokenizer("model-thing");
        try {
            tokenizer.getTokens();
            assertTrue(false);
        }catch(Exception e){}
    }

    public void testSyntaxParser(){
        SyntaxParser parser = new SyntaxParser("functionName(parameter)", new SyntaxParser.TokenConsumer() {
            int tokenIndex = 0;
            @Override
            public void OnValidToken(Tokenizer.Token token) {
                switch (tokenIndex++){
                    case 0:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.FUNCTION_NAME);
                        assertEquals(token.getScript(), "functionName");
                        break;
                    case 1:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.FUNCTION_PARAMETER);
                        assertEquals(token.getScript(), "parameter");
                        break;
                    case 2:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.EOF);
                        break;
                    default:
                        assertTrue(false);
                        break;
                }
            }
        });
        parser.parseScript();

        parser = new SyntaxParser("functionName(parameter , parameter2  )", new SyntaxParser.TokenConsumer() {
            int tokenIndex = 0;
            @Override
            public void OnValidToken(Tokenizer.Token token) {
                switch (tokenIndex++){
                    case 0:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.FUNCTION_NAME);
                        assertEquals(token.getScript(), "functionName");
                        break;
                    case 1:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.FUNCTION_PARAMETER);
                        assertEquals(token.getScript(), "parameter");
                        break;
                    case 2:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.FUNCTION_PARAMETER);
                        assertEquals(token.getScript(), "parameter2");
                        break;
                    case 3:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.EOF);
                        break;
                    default:
                        assertTrue(false);
                        break;
                }
            }
        });
        parser.parseScript();


        parser = new SyntaxParser("modelName.boolValue ? functionName(parameter , parameter2  ) : modelName.stringValue", new SyntaxParser.TokenConsumer() {
            int tokenIndex = 0;
            @Override
            public void OnValidToken(Tokenizer.Token token) {
                switch (tokenIndex++){
                    case 0:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.MODEL_NAME);
                        assertEquals(token.getScript(), "modelName");
                        break;
                    case 1:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.MODEL_FIELD);
                        assertEquals(token.getScript(), "boolValue");
                        break;
                    case 2:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.TERNARY_QUESTION_MARK);
                        assertEquals(token.getScript(), "?");
                        break;
                    case 3:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.FUNCTION_NAME);
                        assertEquals(token.getScript(), "functionName");
                        break;
                    case 4:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.FUNCTION_PARAMETER);
                        assertEquals(token.getScript(), "parameter");
                        break;
                    case 5:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.FUNCTION_PARAMETER);
                        assertEquals(token.getScript(), "parameter2");
                        break;
                    case 6:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.TERNARY_COLON);
                        assertEquals(token.getScript(), ":");
                        break;
                    case 7:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.MODEL_NAME);
                        assertEquals(token.getScript(), "modelName");
                        break;
                    case 8:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.MODEL_FIELD);
                        assertEquals(token.getScript(), "stringValue");
                        break;
                    case 9:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.EOF);
                        break;
                    default:
                        assertTrue(false);
                        break;
                }
            }
        });
        parser.parseScript();


        parser = new SyntaxParser("testName.testField", new SyntaxParser.TokenConsumer() {
            int tokenIndex = 0;
            @Override
            public void OnValidToken(Tokenizer.Token token) {
                switch (tokenIndex++){
                    case 0:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.MODEL_NAME);
                        assertEquals(token.getScript(), "testName");
                        break;
                    case 1:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.MODEL_FIELD);
                        assertEquals(token.getScript(), "testField");
                        break;
                    case 2:
                        assertEquals(token.getTokenType(), Tokenizer.TokenType.EOF);
                        break;
                    default:
                        assertTrue(false);
                        break;
                }
            }
        });
        parser.parseScript();
    }
}