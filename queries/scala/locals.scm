[(block)
(template_body)
(lambda_expression)
(function_declaration)] @scope

(function_declaration
  name: (identifier) @definition.function)

(val_definition
  pattern: (identifier) @definition.var)

(parameters (parameter name: (identifier) @definition.parameter ))
