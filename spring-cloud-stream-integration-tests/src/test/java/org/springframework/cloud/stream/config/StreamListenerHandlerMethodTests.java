/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.config;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.Assert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.cloud.stream.binding.StreamListenerErrorMessages.AMBIGUOUS_MESSAGE_HANDLER_METHOD_ARGUMENTS;
import static org.springframework.cloud.stream.binding.StreamListenerErrorMessages.INPUT_AT_STREAM_LISTENER;
import static org.springframework.cloud.stream.binding.StreamListenerErrorMessages.INVALID_INBOUND_NAME;
import static org.springframework.cloud.stream.binding.StreamListenerErrorMessages.INVALID_MESSAGE_HANDLER_METHOD_PARAMS;
import static org.springframework.cloud.stream.binding.StreamListenerErrorMessages.INVALID_OUTBOUND_NAME;
import static org.springframework.cloud.stream.binding.StreamListenerErrorMessages.INVALID_OUTPUT_VALUES;
import static org.springframework.cloud.stream.binding.StreamListenerErrorMessages.NO_INPUT_DESTINATION;
import static org.springframework.cloud.stream.binding.StreamListenerErrorMessages.RETURN_TYPE_MULTIPLE_OUTBOUND_SPECIFIED;
import static org.springframework.cloud.stream.binding.StreamListenerErrorMessages.RETURN_TYPE_NO_OUTBOUND_SPECIFIED;
import static org.springframework.cloud.stream.binding.StreamListenerErrorMessages.TARGET_BEAN_NOT_EXISTS;

/**
 * @author Marius Bogoevici
 * @author Ilayaperumal Gopinathan
 */
public class StreamListenerHandlerMethodTests {

	@Test
	public void testMethodWIthInputOnStreamListener() throws Exception {
		try {
			SpringApplication.run(TestMethodWIthInputOnStreamListener.class, "--server.port=0");
			fail("Exception expected: "+ INPUT_AT_STREAM_LISTENER);
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).contains(INPUT_AT_STREAM_LISTENER);
		}
	}

	@Test
	public void testReturnTypeWithMultipleOutput() throws Exception {
		try {
			SpringApplication.run(TestReturnTypeWithMultipleOutput.class, "--server.port=0");
			fail("Exception expected: "+ RETURN_TYPE_MULTIPLE_OUTBOUND_SPECIFIED);
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).contains(RETURN_TYPE_MULTIPLE_OUTBOUND_SPECIFIED);
		}
	}

	@Test
	public void testReturnTypeWithNoOutput() throws Exception {
		try {
			SpringApplication.run(TestReturnTypeWithNoOutput.class, "--server.port=0");
			fail("Exception expected: " + RETURN_TYPE_NO_OUTBOUND_SPECIFIED);
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).contains(RETURN_TYPE_NO_OUTBOUND_SPECIFIED);
		}
	}

	@Test
	public void testMethodInputAnnotationWithNoValue() throws Exception {
		try {
			SpringApplication.run(TestMethodInputAnnotationWithNoValue.class, "--server.port=0");
			fail("Exception expected: "+ INVALID_INBOUND_NAME);
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).contains(INVALID_INBOUND_NAME);
		}
	}

	@Test
	public void testMethodOutputAnnotationWithNoValue() throws Exception {
		try {
			SpringApplication.run(TestMethodOutputAnnotationWithNoValue.class, "--server.port=0");
			fail("Exception expected: "+ INVALID_OUTBOUND_NAME);
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).contains(INVALID_OUTBOUND_NAME);
		}
	}

	@Test
	public void testMethodInvalidInboundName() throws Exception {
		try {
			SpringApplication.run(TestMethodInvalidInboundName.class, "--server.port=0");
			fail("Exception expected on using invalid inbound name");
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).contains(TARGET_BEAN_NOT_EXISTS + ": invalid");
		}
	}

	@Test
	public void testMethodInvalidOutboundName() throws Exception {
		try {
			SpringApplication.run(TestMethodInvalidOutboundName.class, "--server.port=0");
			fail("Exception expected on using invalid outbound name");
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).contains(TARGET_BEAN_NOT_EXISTS + ": invalid");
		}
	}

	@Test
	public void testAmbiguousMethodArguments1() throws Exception {
		try {
			SpringApplication.run(TestAmbiguousMethodArguments1.class, "--server.port=0");
			fail("Exception expected: "+ AMBIGUOUS_MESSAGE_HANDLER_METHOD_ARGUMENTS);
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).contains(AMBIGUOUS_MESSAGE_HANDLER_METHOD_ARGUMENTS);
		}
	}

	@Test
	public void testAmbiguousMethodArguments2() throws Exception {
		try {
			SpringApplication.run(TestAmbiguousMethodArguments2.class, "--server.port=0");
			fail("Exception expected:" + AMBIGUOUS_MESSAGE_HANDLER_METHOD_ARGUMENTS);
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).contains(AMBIGUOUS_MESSAGE_HANDLER_METHOD_ARGUMENTS);
		}
	}

	@Test
	public void testMethodWithInputAsMethodAndParameter() throws Exception {
		try {
			SpringApplication.run(TestMethodWithInputAsMethodAndParameter.class, "--server.port=0");
			fail("Exception expected: " + INVALID_MESSAGE_HANDLER_METHOD_PARAMS);
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).contains(INVALID_MESSAGE_HANDLER_METHOD_PARAMS);
		}
	}

	@Test
	public void testMethodWithOutputAsMethodAndParameter() throws Exception {
		try {
			SpringApplication.run(TestMethodWithOutputAsMethodAndParameter.class, "--server.port=0");
			fail("Exception expected:" +  INVALID_OUTPUT_VALUES);
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).startsWith(INVALID_OUTPUT_VALUES);
		}
	}

	@Test
	public void testMethodWithoutInput() throws Exception {
		try {
			SpringApplication.run(TestMethodWithoutInput.class, "--server.port=0");
			fail("Exception expected when inbound target is not set");
		}
		catch (BeanCreationException e) {
			assertThat(e.getCause().getMessage()).contains(NO_INPUT_DESTINATION);
		}
	}

	@Test
	public void testMethodWithMultipleInputParameters() throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(TestMethodWithMultipleInputParameters.class, "--server.port=0");
		Processor processor = context.getBean(Processor.class);
		StreamListenerTestInterfaces.FooInboundChannel1 inboundChannel2 = context.getBean(StreamListenerTestInterfaces.FooInboundChannel1.class);
		String id = UUID.randomUUID().toString();
		final CountDownLatch latch = new CountDownLatch(2);
		((SubscribableChannel) processor.output()).subscribe(new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				Assert.isTrue(message.getPayload().equals("footesting") || message.getPayload().equals("BARTESTING"));
				latch.countDown();
			}
		});
		processor.input().send(MessageBuilder.withPayload("{\"foo\":\"fooTESTing" + "\"}")
				.setHeader("contentType", "application/json").build());
		inboundChannel2.input().send(MessageBuilder.withPayload("{\"bar\":\"bartestING" + "\"}")
				.setHeader("contentType", "application/json").build());
		assertThat(latch.await(1, TimeUnit.SECONDS));
		context.close();
	}

	@Test
	public void testMethodWithMultipleOutputParameters() throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(TestMethodWithMultipleOutputParameters.class, "--server.port=0");
		Processor processor = context.getBean(Processor.class);
		String id = UUID.randomUUID().toString();
		StreamListenerTestInterfaces.FooOutboundChannel1 source2 = context.getBean(StreamListenerTestInterfaces.FooOutboundChannel1.class);
		final CountDownLatch latch = new CountDownLatch(2);
		((SubscribableChannel) processor.output()).subscribe(new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				Assert.isTrue(message.getPayload().equals("testing"));
				Assert.isTrue(message.getHeaders().get("output").equals("output2"));
				latch.countDown();
			}
		});
		((SubscribableChannel) source2.output()).subscribe(new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				Assert.isTrue(message.getPayload().equals("TESTING"));
				Assert.isTrue(message.getHeaders().get("output").equals("output1"));
				latch.countDown();
			}
		});
		processor.input().send(MessageBuilder.withPayload("testING").setHeader("output", "output1").build());
		processor.input().send(MessageBuilder.withPayload("TESTing").setHeader("output", "output2").build());
		assertThat(latch.await(1, TimeUnit.SECONDS));
		context.close();
	}

	@EnableBinding({Processor.class, StreamListenerTestInterfaces.FooOutboundChannel1.class})
	@EnableAutoConfiguration
	public static class TestMethodWithMultipleOutputParameters {

		@StreamListener
		public void receive(@Input(Processor.INPUT) SubscribableChannel input, @Output(Processor.OUTPUT) final MessageChannel output1,
				@Output(StreamListenerTestInterfaces.FooOutboundChannel1.OUTPUT) final MessageChannel output2) {
			input.subscribe(new MessageHandler() {
				@Override
				public void handleMessage(Message<?> message) throws MessagingException {
					if (message.getHeaders().get("output").equals("output1")) {
						output1.send(org.springframework.messaging.support.MessageBuilder.withPayload(message.getPayload().toString().toUpperCase()).build());
					}
					else if (message.getHeaders().get("output").equals("output2")) {
						output2.send(org.springframework.messaging.support.MessageBuilder.withPayload(message.getPayload().toString().toLowerCase()).build());
					}
				}
			});
		}
	}

	@EnableBinding({Sink.class})
	@EnableAutoConfiguration
	public static class TestMethodWithoutInput {

		@StreamListener
		public void receive(StreamListenerTestInterfaces.FooPojo fooPojo) {
		}
	}

	@EnableBinding({Sink.class})
	@EnableAutoConfiguration
	public static class TestMethodWIthInputOnStreamListener {

		@StreamListener
		@Input(Sink.INPUT)
		public void receive(StreamListenerTestInterfaces.FooPojo fooPojo) {
		}
	}

	@EnableBinding({Sink.class})
	@EnableAutoConfiguration
	public static class TestAmbiguousMethodArguments1 {

		@StreamListener(Processor.INPUT)
		public void receive(@Payload StreamListenerTestInterfaces.FooPojo fooPojo, String value) {
		}
	}

	@EnableBinding({Sink.class})
	@EnableAutoConfiguration
	public static class TestAmbiguousMethodArguments2 {

		@StreamListener(Processor.INPUT)
		public void receive(@Payload StreamListenerTestInterfaces.FooPojo fooPojo, @Payload StreamListenerTestInterfaces.BarPojo barPojo) {
		}
	}

	@EnableBinding({Processor.class, StreamListenerTestInterfaces.FooOutboundChannel1.class})
	@EnableAutoConfiguration
	public static class TestReturnTypeWithMultipleOutput {

		@StreamListener
		public String receive(@Input(Processor.INPUT) SubscribableChannel input1, @Output(Processor.OUTPUT) MessageChannel output1,
				@Output(StreamListenerTestInterfaces.FooOutboundChannel1.OUTPUT) MessageChannel output2) {
			return "foo";
		}
	}

	@EnableBinding({Processor.class, StreamListenerTestInterfaces.FooOutboundChannel1.class})
	@EnableAutoConfiguration
	public static class TestReturnTypeWithNoOutput {

		@StreamListener
		public String receive(@Input(Processor.INPUT) SubscribableChannel input1) {
			return "foo";
		}
	}

	@EnableBinding({Processor.class})
	@EnableAutoConfiguration
	public static class TestMethodInputAnnotationWithNoValue {

		@StreamListener
		public void receive(@Input SubscribableChannel input) {
		}
	}

	@EnableBinding({Processor.class})
	@EnableAutoConfiguration
	public static class TestMethodOutputAnnotationWithNoValue {

		@StreamListener
		public void receive(@Input(Processor.OUTPUT) SubscribableChannel input, @Output MessageChannel output) {
		}
	}

	@EnableBinding({Sink.class})
	@EnableAutoConfiguration
	public static class TestMethodInvalidInboundName {

		@StreamListener
		public void receive(@Input("invalid") SubscribableChannel input) {
		}
	}

	@EnableBinding({Processor.class})
	@EnableAutoConfiguration
	public static class TestMethodInvalidOutboundName {

		@StreamListener
		public void receive(@Input(Processor.INPUT) SubscribableChannel input, @Output("invalid") MessageChannel output) {
		}
	}

	@EnableBinding({Sink.class})
	@EnableAutoConfiguration
	public static class TestMethodWithInputAsMethodAndParameter {

		@StreamListener
		public void receive(@Input(Sink.INPUT) StreamListenerTestInterfaces.FooPojo fooPojo) {
		}
	}

	@EnableBinding({Processor.class, StreamListenerTestInterfaces.FooOutboundChannel1.class})
	@EnableAutoConfiguration
	public static class TestMethodWithOutputAsMethodAndParameter {

		@StreamListener
		@Output(StreamListenerTestInterfaces.FooOutboundChannel1.OUTPUT)
		public void receive(@Input(Processor.INPUT) SubscribableChannel input, @Output(Processor.OUTPUT) final MessageChannel output1) {
			input.subscribe(new MessageHandler() {
				@Override
				public void handleMessage(Message<?> message) throws MessagingException {
					output1.send(org.springframework.messaging.support.MessageBuilder.withPayload(message.getPayload().toString().toUpperCase()).build());
				}
			});
		}
	}

	@EnableBinding({Processor.class, StreamListenerTestInterfaces.FooInboundChannel1.class})
	@EnableAutoConfiguration
	public static class TestMethodWithMultipleInputParameters {

		@StreamListener
		public void receive(@Input(Processor.INPUT) SubscribableChannel input1, @Input(StreamListenerTestInterfaces.FooInboundChannel1.INPUT) SubscribableChannel input2,
				final @Output(Processor.OUTPUT) MessageChannel output) {
			input1.subscribe(new MessageHandler() {
				@Override
				public void handleMessage(Message<?> message) throws MessagingException {
					output.send(org.springframework.messaging.support.MessageBuilder.withPayload(message.getPayload().toString().toUpperCase()).build());
				}
			});
			input2.subscribe(new MessageHandler() {
				@Override
				public void handleMessage(Message<?> message) throws MessagingException {
					output.send(org.springframework.messaging.support.MessageBuilder.withPayload(message.getPayload().toString().toUpperCase()).build());
				}
			});
		}
	}
}