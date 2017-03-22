CREATE TABLE product_nutrition_facts(
  product_id INT NOT NULL,
  nutrition_facts_key VARCHAR(255) NOT NULL,
  nutrition_facts VARCHAR(255),
  CONSTRAINT `PRIMARY` PRIMARY KEY (product_id, nutrition_facts_key),
  CONSTRAINT FK_product_id FOREIGN KEY (product_id) REFERENCES products(id)
);
CREATE INDEX FK_product_id ON products(id);