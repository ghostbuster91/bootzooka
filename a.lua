local require_ok, locals = pcall(require, 'nvim-treesitter.locals')
local _, utils = pcall(require, 'nvim-treesitter.utils')
local _, parsers = pcall(require, 'nvim-treesitter.parsers')
local _, queries = pcall(require, 'nvim-treesitter.query')
local buf = 0

local scope_nodes = locals.get_scopes(buf)
local definition_nodes = locals.get_locals(buf)
local nodes = ""
-- for _, d in pairs(definition_nodes) do
--   local node = utils.get_at_path(d, 'definition.var.node')
--       or utils.get_at_path(d, 'definition.parameter.node')
--   if node then
--     local name = vim.treesitter.query.get_node_text(node, buf)
--     nodes = nodes .. " " .. name
--   end
-- end
-- print(nodes)
nodes = ""
for _, d in pairs(scope_nodes) do
  local node = utils.get_at_path(d, 'definition.var.node')
      or utils.get_at_path(d, 'definition.parameter.node')
  if node then
    local name = vim.treesitter.query.get_node_text(node, buf)
    nodes = nodes .. " // " .. name
  end
end
print(nodes)

-- for index, value in ipairs(definition_nodes) do
--   print(vim.inspect(value.tree))
-- end
-- print(definition_nodes)
