package com.yan.sb;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.alibaba.sentinel.annotation.SentinelRestTemplate;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class SbApplication {
	@Bean
	@LoadBalanced
	@SentinelRestTemplate(fallback = "fallbackHandler")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	public String fallbackHandler(long s, BlockException ex) {
		// Do some log here.
		ex.printStackTrace();
		return "Oops, error occurred at " + s;
	}

	public static void main(String[] args) {
		SpringApplication.run(SbApplication.class, args);
	}

	@RestController
	public class TestController {

		private final RestTemplate restTemplate;

		@Autowired
		public TestController(RestTemplate restTemplate) {this.restTemplate = restTemplate;}

		@RequestMapping(value = "/echo/{str}", method = RequestMethod.GET)
		public String echo(@PathVariable String str) {
			return restTemplate.getForObject("http://nacos-producer//hello/" + str, String.class);
		}

		@RequestMapping(value = "/hello", method = RequestMethod.GET)
		public String hello() {
			String result = restTemplate.getForObject("http://nacos-producer/hello", String.class);
			return  "result: "+result;
		}
	}
}
