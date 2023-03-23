(val_definition
  pattern: (identifier) @definition.var)

(infix_expression right: (block)) @test

(class_definition
  body: (template_body)? @class.inner) @class.outer

(object_definition
  body: (template_body)? @class.inner) @class.outer

(function_definition
  body: (indented_block) @function.inner) @function.outer

(function_definition
  body: (block) @function.inner) @function.outer

(function_definition
  body: (field_expression) @function.inner) @function.outer


(function_definition
  body: (call_expression) @function.inner) @function.outer

(parameter
  name: (identifier) @parameter.inner) @parameter.outer

(class_parameter name: (identifier) @parameter.inner) @parameter.outer

(comment) @comment.outer
